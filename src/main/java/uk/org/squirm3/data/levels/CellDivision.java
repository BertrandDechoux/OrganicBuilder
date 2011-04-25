package uk.org.squirm3.data.levels;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class CellDivision extends Level {

    public CellDivision(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "celldivision", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        final Random PRNG = new Random(); // a prng for use when resetting atoms
        // place and bond N atoms to form a loop
        final int N = 18;
        final int pos_y[] = {-1, 0, 1, 2, 3, 4, 5, 6, 6, 6, 5, 4, 3, 2, 1, 0,
                -1, -1}; // reading
                         // clockwise
                         // from
                         // the
                         // top-left
                         // corner
                         // (y
                         // is
                         // down)
        final int pos_x[] = {-1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 1, 1, 1, 1,
                1, 1, 1, 0};
        int i; // atom index incremented in loops but also used elsewhere
               // without resetting
        final IPhysicalPoint mobilePoint = new MobilePoint();
        for (i = 0; i < N; i++) {
            final int state = i == N - 1 || i == N / 2 - 1 ? 3 : 2;
            mobilePoint.setPositionX(size * 4.0f + pos_x[i] * size * 2.0f);
            mobilePoint.setPositionY(size * 7.0f + pos_y[i] * size * 2.0f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 0, state);

        }
        for (int j = 0; j < N; j++) {
            atoms[j].bondWith(atoms[(j + 1) % N]);
        }

        // place and bond six atoms to form a template
        // (ensure each type is used once (to allow multiple copies to be made,
        // if they want))
        final int[][] genomes = {{0, 1, 3, 2}, {3, 1, 2, 0}, {2, 3, 0, 1},
                {1, 2, 0, 3}, {0, 3, 2, 1}};
        final int which_genome = PRNG.nextInt(5);
        final int so_far = i;
        for (; i < so_far + 6; i++) {
            mobilePoint.setPositionX(size * 4.0f);
            mobilePoint.setPositionY(size * 7.0f + (i - so_far) * size * 2.0f);
            if (i - so_far == 0) {
                atoms[i] = new Atom(mobilePoint, 4, 1); // 'e' at the top
                atoms[i].bondWith(atoms[N - 1]);
            } else if (i - so_far == 5) {
                atoms[i] = new Atom(mobilePoint, 5, 1); // 'f' at the bottom
                atoms[i].bondWith(atoms[N / 2 - 1]);
                atoms[i].bondWith(atoms[i - 1]);
            } else {
                atoms[i] = new Atom(mobilePoint, genomes[which_genome][i
                        - so_far - 1], 1);
                atoms[i].bondWith(atoms[i - 1]);
            }
        }
        // set one of the free-floating atoms to be a killer enzyme we must
        // exclude from the cell

        if (createAtoms(configuration.getNumberOfAtoms() - (N + 6), new int[]{
                0, 1, 2, 0, 3, 4, 0, 5}, 6 * size, configuration.getWidth(), 0,
                configuration.getHeight(), atoms)) {
            atoms[atoms.length - 1] = new Atom(
                    atoms[atoms.length - 1].getPhysicalPoint(),
                    Atom.KILLER_TYPE, 0);
            return atoms;
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {

        // evaluation of this level is quite tricky. we could be strict and
        // insist on neat membranes but that is
        // not really the intention.

        // pseudocode: non-embedded connected components, identification of the
        // copies, check for template copy

        final LinkedList components = new LinkedList(); // stores a list of
        // LinkedList's, the
        // components
        for (final Atom a : atoms) {
            // is this atom already in a connected component?
            boolean already_seen = false;
            for (int iComponent = 0; iComponent < components.size(); iComponent++) {
                if (((LinkedList) components.get(iComponent)).contains(a)) {
                    already_seen = true;
                    break;
                }
            }
            if (already_seen) {
                continue;
            }
            // create a new connected component starting from this atom
            final LinkedList component = new LinkedList();
            a.getAllConnectedAtoms(component);
            if (component.size() > 6) {
                components.add(component);
            }
        }

        if (components.size() != 2) {
            // two large components
            return getError(1);
        }
        // neither component should be inside the other
        {
            final Polygon poly[] = new Polygon[2];
            // assemble the two polygons (doesn't matter if they're a bit messy
            // at places)
            for (int iComp = 0; iComp < 2; iComp++) {
                final int NP = ((LinkedList) components.get(iComp)).size();
                final int px[] = new int[NP], py[] = new int[NP];
                for (int i = 0; i < NP; i++) {
                    final Atom a = (Atom) ((LinkedList) components.get(iComp))
                            .get(i);
                    px[i] = (int) a.getPhysicalPoint().getPositionX();
                    py[i] = (int) a.getPhysicalPoint().getPositionY();
                }
                poly[iComp] = new Polygon(px, py, NP);
            }
            // check for either polygon having a point inside the other
            // (given that bond-crossing is forbidden, we expect this to be a
            // complete test of separatedness)
            for (int iComp = 0; iComp < 2; iComp++) {
                final LinkedList c = (LinkedList) components.get(iComp);
                final int NP = c.size();
                for (int i = 0; i < NP; i++) {
                    final Atom a = (Atom) c.get(i);
                    // is this point inside the other polygon?
                    if (poly[1 - iComp].contains(new Point2D.Float(a
                            .getPhysicalPoint().getPositionX(), a
                            .getPhysicalPoint().getPositionY()))) {
                        return getError(2);
                    }
                }
            }
        }

        // let's enforce that the template is a sequence of 2-connected atoms
        // starting with 'e'
        // and ending with 'f', with each end connected to 3+ connected atoms,
        // and only types a-d in between
        final Atom heads[] = new Atom[2]; // will put the pointers to the two
                                          // 'e' ends
        // here
        int n_found = 0;
        for (int i = 0; i < atoms.length && n_found < 2; i++) {
            final Atom a = atoms[i];
            if (a.getType() == 4 && a.getState() != 0
                    && a.getBonds().size() == 2) {
                heads[n_found++] = a;
            }
        }
        if (n_found < 2) {
            return getError(3);
        }
        // each head should be in a separate component
        final LinkedList c1 = (LinkedList) components.get(0), c2 = (LinkedList) components
                .get(1);
        if (c1.contains(heads[0]) && !c2.contains(heads[1])
                || c2.contains(heads[0]) && !c1.contains(heads[1])) {
            return getError(4);
        }
        // work down each template, adding the type of each 2-connected atom to
        // sequence[i]
        final String sequence[] = {new String(), new String()};
        for (int iCell = 0; iCell < 2; iCell++) {
            final LinkedList seen = new LinkedList();
            Atom current = heads[iCell];
            seen.add(current);
            sequence[iCell] = "e"; // let's get things started
            if (((Atom) current.getBonds().getFirst()).getBonds().size() == 2) {
                current = (Atom) current.getBonds().getFirst();
            } else {
                current = (Atom) current.getBonds().getLast();
            }
            while (sequence[iCell].length() < 10) {
                // if the current atom has other than 2 bonds then we are done
                if (current.getBonds().size() != 2) {
                    break;
                }
                // append the type letter (a-f) to the string
                sequence[iCell] += Atom.type_code.charAt(current.getType());
                // add the current atom to the list so that we will know we have
                // seen it before
                seen.add(current);
                // move onto the next bond (we know this atom has exactly two)
                if (seen.contains(current.getBonds().getFirst())) {
                    current = (Atom) current.getBonds().get(1);
                } else {
                    current = (Atom) current.getBonds().getFirst();
                }
            }
            // System.out.println(sequence[iCell]);
            if (sequence[iCell].length() != 6
                    || sequence[iCell].charAt(0) != 'e'
                    || sequence[iCell].charAt(5) != 'f') {
                // parameter :
                // "Incorrect template sequence detected: "+sequence[iCell];
                return getError(5);
            }
        }
        if (sequence[0].compareTo(sequence[1]) != 0) {
            return getError(6);
        }

        return null;
    }
}
