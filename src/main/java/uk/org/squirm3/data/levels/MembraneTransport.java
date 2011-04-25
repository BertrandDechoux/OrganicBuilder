package uk.org.squirm3.data.levels;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class MembraneTransport extends Level {

    public MembraneTransport(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "membranetransport", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        final IPhysicalPoint mobilePoint = new MobilePoint();
        // place and bond N atoms to form a loop
        final int N = 12;
        final int pos_y[] = {-1, 0, 1, 2, 3, 3, 3, 2, 1, 0, -1, -1}; // reading
        // clockwise
        // from the
        // top-left
        // corner (y is
        // down)
        final int pos_x[] = {-1, -1, -1, -1, -1, 0, 1, 1, 1, 1, 1, 0};
        int i; // atom index incremented in loops but also used elsewhere
               // without resetting
        for (i = 0; i < N; i++) {
            int state;
            if (i == 0) {
                state = 3;
            } else if (i == 1) {
                state = 4;
            } else {
                state = 2;
            }
            mobilePoint.setPositionX(size * 4.0f + pos_x[i] * size * 2.0f);
            mobilePoint.setPositionY(size * 7.0f + pos_y[i] * size * 2.0f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 0, state);
        }
        for (int j = 0; j < N; j++) {
            atoms[j].bondWith(atoms[(j + 1) % N]);
        }
        // put two e1 atoms and one b1 atom inside
        final int so_far = i;
        for (; i < so_far + 3; i++) {
            mobilePoint.setPositionY(size * (7.0f + (i - so_far) * 2.0f));
            mobilePoint.setPositionX(size * 4.0f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, i - so_far == 0 ? 1 : 4, 1);
        }
        // create the others atoms
        if (createAtoms(configuration.getNumberOfAtoms() - (N + 1),
                configuration.getTypes(), 7 * size, configuration.getWidth(),
                0, configuration.getHeight(), atoms)) {
            return atoms;
        }
        return null;

    }

    @Override
    public String evaluate(final Atom[] atoms) {
        // starting from atom 0 there should be a neat closed loop of a atoms
        final LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        // (each atom in the connected group should of type 'a' and have exactly
        // two bonds (hence a neat loop))
        final int x_points[] = new int[joined.size()], y_points[] = new int[joined
                .size()];
        final Iterator it = joined.iterator();
        int i = 0;
        while (it.hasNext()) {
            final Atom a = (Atom) it.next();
            if (a.getType() != 0 || a.getBonds().size() != 2) {
                return getError(1);
            }
            x_points[i] = (int) a.getPhysicalPoint().getPositionX(); // (need
                                                                     // these
                                                                     // for
                                                                     // polygon
                                                                     // check,
                                                                     // below)
            y_points[i] = (int) a.getPhysicalPoint().getPositionY();
            i++;
        }
        // inside should be the original 'b' atom, and all the 'f' atoms, and
        // nothing else
        final Atom b1 = atoms[12]; // see the setup code for this level
        final Polygon poly = new Polygon(x_points, y_points, joined.size());
        if (!poly.contains(new Point2D.Float(b1.getPhysicalPoint()
                .getPositionX(), b1.getPhysicalPoint().getPositionY()))) {
            return getError(2);
        }
        // check the other atoms (want: f's inside, other's outside)
        for (i = joined.size() + 1; i < atoms.length; i++) {
            final Atom a = atoms[i];
            if (a.getType() == 5
                    && !poly.contains(new Point2D.Float(a.getPhysicalPoint()
                            .getPositionX(), a.getPhysicalPoint()
                            .getPositionY()))) {
                return getError(3);
            } else if (a.getType() != 5
                    && poly.contains(new Point2D.Float(a.getPhysicalPoint()
                            .getPositionX(), a.getPhysicalPoint()
                            .getPositionY()))) {
                return getError(4);
            }
        }
        return null;
    }
}
