package uk.org.squirm3.engine;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.DraggingPoint;
import uk.org.squirm3.model.Reaction;

final class Collider {

    // ------- data ---------
    private final List<? extends Atom> atoms;

    // structures for space-division speed-up:
    private final List buckets[][]; // each bucket has a list of the indices of
                                    // the sq3Atoms contained within it
    private final int n_buckets_x, n_buckets_y; // the horizontal and vertical
                                                // dimensions are divided into
                                                // this many buckets
    private final int width, height;
    // we store the size to ensure we access the right bucket (dimensions might
    // change)
    protected float bucket_width, bucket_height;

    private float MAX_SPEED = 5.0f; // recomputed when R changes (thanks Ralph)
    private final Set reactedAtoms = new HashSet();

    // ------- methods ---------
    public Collider(final List<? extends Atom> atoms, final int w, final int h) {
        this.atoms = atoms;
        // size the buckets structure so each bucket is approximately R in size
        // (approx 1 atom per bucket)
        final float R = Atom.getAtomSize();
        // recompute MAX_SPEED to allow for the new R
        MAX_SPEED = 5.0f * R / 22.0f; // (thanks Ralph)
        n_buckets_x = Math.round(w / (1.0f * R));
        n_buckets_y = Math.round(h / (1.0f * R));
        // (else div0 error)
        buckets = new LinkedList[n_buckets_x][n_buckets_y]; // (garbage
                                                            // collection takes
                                                            // care of old
                                                            // array)
        // allocate each
        for (int x = 0; x < n_buckets_x; x++) {
            for (int y = 0; y < n_buckets_y; y++) {
                buckets[x][y] = new LinkedList();
            }
        }
        width = w;
        height = h;
        bucket_width = w / (float) n_buckets_x;
        bucket_height = h / (float) n_buckets_y;
        // insert any atoms currently present
        for (int i = 0; i < atoms.size(); i++) {
            final Atom a = atoms.get(i);
            int bucket_x, bucket_y;
            bucket_x = whichBucketX(a.getPhysicalPoint().getPositionX());
            bucket_y = whichBucketY(a.getPhysicalPoint().getPositionY());
            buckets[bucket_x][bucket_y].add(new Integer(i));
        }
    }

    // TODO protect the collection : copy or immutable view
    public List<? extends Atom> getAtoms() {
        return atoms;
    }

    private int whichBucketX(final float x) {
        int w = (int) Math.floor(x / bucket_width);
        if (w < 0) {
            w = 0;
        } else if (w >= n_buckets_x) {
            w = n_buckets_x - 1;
        }
        return w;
    }

    private int whichBucketY(final float y) {
        int w = (int) Math.floor(y / bucket_height);
        if (w < 0) {
            w = 0;
        } else if (w >= n_buckets_y) {
            w = n_buckets_y - 1;
        }
        return w;
    }

    // straight Euler method computation of spring forces per timestep
    // uses a maximum speed limiter to prevent numerical problems
    // doesn't lose overall speed however, since force computation typically
    // overestimates anyway
    // but reliable and quick
    // disadvantage: bonded atom groups lose group momentum as speed limiter
    // kicks in
    // some possible alternative physics:
    // - a hard-sphere type physics, where instead of a constant timestep we
    // search for future
    // collisions between the spheres and run the sim forward to that point, and
    // recompute their
    // velocities as a result of the collision. might be promising to explore
    // for OB - but how to include
    // bonds (and dragging)?
    // - a lattice-based physics can run very fast indeed but doesn't look as
    // satisfying
    public void doTimeStep(final DraggingPoint draggingPoint,
            final List reactions) {
        // boolean is_dragging,int which_being_dragged, int mouse_x,int mouse_y
        // COMPUTE AND REACT
        // we shuffle the reactions list in order to prevent any reaction
        // artefacts, since
        // reactions are applied as they are found, and conflicting reactions
        // would only ever have
        // the first one applied, eg. with a1c1->a2c2 and x1c1->x4c4, only the
        // first version would apply
        // to any a1c1 pairs. Now each reaction has an equal chance of being
        // chosen.
        Collections.shuffle(reactions);

        // starting over for this iteration
        reactedAtoms.clear();
        // TODO the collider should not be responsible for the mangement of
        // reactions

        final float R = Atom.getAtomSize();
        final float diam = 2.0f * R;
        final float diam2 = diam * diam;

        for (int i = 0; i < atoms.size(); i++) {
            Atom a = atoms.get(i);
            // bounce off the walls
            if (a.getPhysicalPoint().getPositionX() < R) {
                a.getPhysicalPoint().setSpeedX(
                        a.getPhysicalPoint().getSpeedX()
                                + getForce(R
                                        - a.getPhysicalPoint().getPositionX()));
            }
            if (a.getPhysicalPoint().getPositionY() < R) {
                a.getPhysicalPoint().setSpeedY(
                        a.getPhysicalPoint().getSpeedY()
                                + getForce(R
                                        - a.getPhysicalPoint().getPositionY()));
            }
            if (a.getPhysicalPoint().getPositionX() > width - R) {
                a.getPhysicalPoint().setSpeedX(
                        a.getPhysicalPoint().getSpeedX()
                                - getForce(a.getPhysicalPoint().getPositionX()
                                        - (width - R)));
            }
            if (a.getPhysicalPoint().getPositionY() > height - R) {
                a.getPhysicalPoint().setSpeedY(
                        a.getPhysicalPoint().getSpeedY()
                                - getForce(a.getPhysicalPoint().getPositionY()
                                        - (height - R)));
            }
            // bounce off other atoms that are within 2R distance of this one
            // what square radius must we search for neighbours?
            final int rx = (int) Math.ceil(diam / bucket_width);
            final int ry = (int) Math.ceil(diam / bucket_height);
            // what bucket is the atom in?
            final int wx = whichBucketX(a.getPhysicalPoint().getPositionX());
            final int wy = whichBucketY(a.getPhysicalPoint().getPositionY());
            // accumulate the list of any atoms in this square radius (clamped
            // to the valid area)
            for (int x = Math.max(0, wx - rx); x <= Math.min(n_buckets_x - 1,
                    wx + rx); x++) {
                for (int y = Math.max(0, wy - ry); y <= Math.min(
                        n_buckets_y - 1, wy + ry); y++) {
                    // add each atom that is in this bucket
                    final Iterator it = buckets[x][y].listIterator();
                    while (it.hasNext()) {
                        final int iOther = ((Integer) it.next()).intValue();
                        if (iOther <= i) {
                            continue; // using Newton's "action&reaction" as a
                        }
                        // shortcut
                        Atom b = atoms.get(iOther);
                        if (new Point2D.Float(a.getPhysicalPoint()
                                .getPositionX(), a.getPhysicalPoint()
                                .getPositionY()).distanceSq(new Point2D.Float(b
                                .getPhysicalPoint().getPositionX(), b
                                .getPhysicalPoint().getPositionY())) < diam2) {
                            // this is a collision - can any reactions apply to
                            // these two atoms?
                            if (!a.isKiller() && !b.isKiller()) {
                                for (int twice = 0; twice < 2
                                        && !reactedAtoms.contains(a)
                                        && !reactedAtoms.contains(b); twice++) {
                                    // try each reaction in turn
                                    final Iterator iterator = reactions
                                            .listIterator();
                                    while (iterator.hasNext()
                                            && !reactedAtoms.contains(a)
                                            && !reactedAtoms.contains(b)) {
                                        if (((Reaction) iterator.next()).tryOn(
                                                a, b)) {
                                            reactedAtoms.add(a);
                                            reactedAtoms.add(b);
                                        }
                                    }
                                    // now swap a and b and try again
                                    final Atom temp = a;
                                    a = b;
                                    b = temp;
                                }
                            } else {
                                // the killer atom breaks the other atoms bonds
                                // (unless other is an 'a' atom)
                                if (a.isKiller()) {
                                    if (b.getType() != 0) {
                                        b.breakAllBonds();
                                    }
                                } else {
                                    if (a.getType() != 0) {
                                        a.breakAllBonds();
                                    }
                                }
                            }
                            // atoms bounce off other atoms
                            final float sep = (float) new Point2D.Float(a
                                    .getPhysicalPoint().getPositionX(), a
                                    .getPhysicalPoint().getPositionY())
                                    .distance(new Point2D.Float(b
                                            .getPhysicalPoint().getPositionX(),
                                            b.getPhysicalPoint().getPositionY()));
                            final float force = getForce(diam - sep);
                            // push from the other atom
                            final float dx = force
                                    * (a.getPhysicalPoint().getPositionX() - b
                                            .getPhysicalPoint().getPositionX())
                                    / sep;
                            final float dy = force
                                    * (a.getPhysicalPoint().getPositionY() - b
                                            .getPhysicalPoint().getPositionY())
                                    / sep;
                            a.getPhysicalPoint().setSpeedX(
                                    a.getPhysicalPoint().getSpeedX() + dx);
                            a.getPhysicalPoint().setSpeedY(
                                    a.getPhysicalPoint().getSpeedY() + dy);
                            b.getPhysicalPoint().setSpeedX(
                                    b.getPhysicalPoint().getSpeedX() - dx); // using
                                                                            // Newton's
                                                                            // "action&reaction"
                                                                            // as
                                                                            // a
                                                                            // shortcut
                            b.getPhysicalPoint().setSpeedY(
                                    b.getPhysicalPoint().getSpeedY() - dy);
                        }
                    }
                }
            }
            // bonds act like springs
            final Iterator it = a.getBonds().iterator();
            while (it.hasNext()) {
                final Atom other = (Atom) it.next();
                final float sep = (float) new Point2D.Float(a
                        .getPhysicalPoint().getPositionX(), a
                        .getPhysicalPoint().getPositionY())
                        .distance(new Point2D.Float(other.getPhysicalPoint()
                                .getPositionX(), other.getPhysicalPoint()
                                .getPositionY()));
                final float force = getForce(sep - diam) / 4.0f; // this
                                                                 // determines
                // the bond spring
                // stiffness
                // pull towards the other atom
                final float dx = force
                        * (other.getPhysicalPoint().getPositionX() - a
                                .getPhysicalPoint().getPositionX()) / sep;
                final float dy = force
                        * (other.getPhysicalPoint().getPositionY() - a
                                .getPhysicalPoint().getPositionY()) / sep;
                a.getPhysicalPoint().setSpeedX(
                        a.getPhysicalPoint().getSpeedX() + dx);
                a.getPhysicalPoint().setSpeedY(
                        a.getPhysicalPoint().getSpeedY() + dy);
            }
            // the user can pull atoms about using the mouse
            if (draggingPoint != null
                    && draggingPoint.getWhichBeingDragging() == i) {
                // normalise the pull vector
                float pullX = draggingPoint.getX()
                        - a.getPhysicalPoint().getPositionX();
                float pullY = draggingPoint.getY()
                        - a.getPhysicalPoint().getPositionY();
                final float dist = (float) Math.sqrt(pullX * pullX + pullY
                        * pullY);
                pullX /= dist;
                pullY /= dist;
                a.getPhysicalPoint().setSpeedX(
                        a.getPhysicalPoint().getSpeedX() + 2.0f * pullX);
                a.getPhysicalPoint().setSpeedY(
                        a.getPhysicalPoint().getSpeedY() + 2.0f * pullY);

            }
            // limit the velocity of each atom to prevent numerical problems
            final float speed = (float) Math.sqrt(a.getPhysicalPoint()
                    .getSpeedX()
                    * a.getPhysicalPoint().getSpeedX()
                    + a.getPhysicalPoint().getSpeedY()
                    * a.getPhysicalPoint().getSpeedY());
            if (speed > MAX_SPEED) {
                a.getPhysicalPoint().setSpeedX(
                        a.getPhysicalPoint().getSpeedX() * MAX_SPEED / speed);
                a.getPhysicalPoint().setSpeedY(
                        a.getPhysicalPoint().getSpeedY() * MAX_SPEED / speed);
            }
        }

        // MOVE ATOMS
        for (int i = 0; i < atoms.size(); i++) {
            final Atom a = atoms.get(i);
            if (a.isStuck()) {
                continue; // special atoms that don't move
            }

            int current_bucket_x, current_bucket_y;
            current_bucket_x = whichBucketX(a.getPhysicalPoint().getPositionX());
            current_bucket_y = whichBucketY(a.getPhysicalPoint().getPositionY());

            a.getPhysicalPoint().setPositionX(
                    a.getPhysicalPoint().getPositionX()
                            + atoms.get(i).getPhysicalPoint().getSpeedX());
            a.getPhysicalPoint().setPositionY(
                    a.getPhysicalPoint().getPositionY()
                            + atoms.get(i).getPhysicalPoint().getSpeedY());

            int new_bucket_x, new_bucket_y;
            new_bucket_x = whichBucketX(a.getPhysicalPoint().getPositionX());
            new_bucket_y = whichBucketY(a.getPhysicalPoint().getPositionY());

            // do we need to move the atom to a new bucket?
            if (new_bucket_x != current_bucket_x
                    || new_bucket_y != current_bucket_y) {
                // remove the atom index from the list
                final java.util.List list = buckets[current_bucket_x][current_bucket_y];
                final ListIterator it = list.listIterator(0);
                while (it.hasNext()) {
                    if (((Integer) it.next()).intValue() == i) {
                        it.remove();
                    }
                }
                buckets[new_bucket_x][new_bucket_y].add(new Integer(i));
            }
        }

    }

    private float getForce(final float d) {
        final float R = Atom.getAtomSize();
        return 1.0f * d * 22.0f / R; // what is the overlap/overstretch force
                                     // for distance d?
        // (now inversely proportional to R, thanks Ralph)
    }

}
