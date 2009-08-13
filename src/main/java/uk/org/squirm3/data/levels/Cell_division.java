package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

/**
 * ${my.copyright}
 */
public class Cell_division extends Level {

    public Cell_division(String title, String challenge, String hint, String[] errors,
                         Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }


    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        Random PRNG = new Random(); // a prng for use when resetting atoms
        // place and bond N atoms to form a loop
        final int N = 18;
        int pos_y[] = {-1, 0, 1, 2, 3, 4, 5, 6, 6, 6, 5, 4, 3, 2, 1, 0, -1, -1}; // reading clockwise from the top-left corner (y is down)
        int pos_x[] = {-1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0};
        int i; // atom index incremented in loops but also used elsewhere without resetting
        IPhysicalPoint mobilePoint = new MobilePoint();
        for (i = 0; i < N; i++) {
            final int state = (i == N - 1 || i == N / 2 - 1) ? 3 : 2;
            mobilePoint.setPositionX(size * 4.0f + pos_x[i] * size * 2.0f);
            mobilePoint.setPositionY(size * 7.0f + pos_y[i] * size * 2.0f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 0, state);

        }
        for (int j = 0; j < N; j++) atoms[j].bondWith(atoms[(j + 1) % N]);


        // place and bond six atoms to form a template
        // (ensure each type is used once (to allow multiple copies to be made, if they want))
        int[][] genomes = {{0, 1, 3, 2}, {3, 1, 2, 0}, {2, 3, 0, 1}, {1, 2, 0, 3}, {0, 3, 2, 1}};
        int which_genome = PRNG.nextInt(5);
        int so_far = i;
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
                atoms[i] = new Atom(mobilePoint, genomes[which_genome][(i - so_far) - 1], 1);
                atoms[i].bondWith(atoms[i - 1]);
            }
        }
        // set one of the free-floating atoms to be a killer enzyme we must exclude from the cell

        if (createAtoms(configuration.getNumberOfAtoms() - (N + 6), new int[]{0, 1, 2, 0, 3, 4, 0, 5}, 6 * size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
            atoms[atoms.length - 1] = new Atom(atoms[atoms.length - 1].getPhysicalPoint(), Atom.KILLER_TYPE, 0);
            return atoms;
        }
        return null;
    }

    public String evaluate(Atom[] atoms) {

        // evaluation of this level is quite tricky. we could be strict and insist on neat membranes but that is
        // not really the intention.

        // pseudocode: non-embedded connected components, identification of the copies, check for template copy

        LinkedList components = new LinkedList(); // stores a list of LinkedList's, the components
        for (int i = 0; i < atoms.length; i++) {
            Atom a = atoms[i];
            // is this atom already in a connected component?
            boolean already_seen = false;
            for (int iComponent = 0; iComponent < components.size(); iComponent++) {
                if (((LinkedList) components.get(iComponent)).contains(a)) {
                    already_seen = true;
                    break;
                }
            }
            if (already_seen) continue;
            // create a new connected component starting from this atom
            LinkedList component = new LinkedList();
            a.getAllConnectedAtoms(component);
            if (component.size() > 6) // only interested in larger groups
                components.add(component);
        }

        if (components.size() != 2)  // lets enforce that there should be exactly two large components
            return getError(1);
        // neither component should be inside the other
        {
            Polygon poly[] = new Polygon[2];
            // assemble the two polygons (doesn't matter if they're a bit messy at places)
            for (int iComp = 0; iComp < 2; iComp++) {
                final int NP = ((LinkedList) components.get(iComp)).size();
                int px[] = new int[NP],
                        py[] = new int[NP];
                for (int i = 0; i < NP; i++) {
                    Atom a = ((Atom) ((LinkedList) components.get(iComp)).get(i));
                    px[i] = (int) a.getPhysicalPoint().getPositionX();
                    py[i] = (int) a.getPhysicalPoint().getPositionY();
                }
                poly[iComp] = new Polygon(px, py, NP);
            }
            // check for either polygon having a point inside the other
            // (given that bond-crossing is forbidden, we expect this to be a complete test of separatedness)
            for (int iComp = 0; iComp < 2; iComp++) {
                LinkedList c = (LinkedList) components.get(iComp);
                int NP = c.size();
                for (int i = 0; i < NP; i++) {
                    Atom a = (Atom) c.get(i);
                    // is this point inside the other polygon?
                    if (poly[1 - iComp].contains(new Point2D.Float(a.getPhysicalPoint().getPositionX(), a.getPhysicalPoint().getPositionY())))
                        return getError(2);
                }
            }
        }

        // let's enforce that the template is a sequence of 2-connected atoms starting with 'e'
        // and ending with 'f', with each end connected to 3+ connected atoms, and only types a-d in between
        Atom heads[] = new Atom[2]; // will put the pointers to the two 'e' ends here
        int n_found = 0;
        for (int i = 0; i < atoms.length && n_found < 2; i++) {
            Atom a = atoms[i];
            if (a.getType() == 4 && a.getState() != 0 && a.getBonds().size() == 2) heads[n_found++] = a;
        }
        if (n_found < 2)
            return getError(3);
        // each head should be in a separate component
        LinkedList c1 = (LinkedList) components.get(0), c2 = (LinkedList) components.get(1);
        if ((c1.contains(heads[0]) && !c2.contains(heads[1])) || (c2.contains(heads[0]) && !c1.contains(heads[1])))
            return getError(4);
        // work down each template, adding the type of each 2-connected atom to sequence[i]
        String sequence[] = {new String(), new String()};
        for (int iCell = 0; iCell < 2; iCell++) {
            LinkedList seen = new LinkedList();
            Atom current = heads[iCell];
            seen.add(current);
            sequence[iCell] = "e"; // let's get things started
            if (((Atom) current.getBonds().getFirst()).getBonds().size() == 2)
                current = (Atom) current.getBonds().getFirst();
            else
                current = (Atom) current.getBonds().getLast();
            while (sequence[iCell].length() < 10) {
                // if the current atom has other than 2 bonds then we are done
                if (current.getBonds().size() != 2) break;
                // append the type letter (a-f) to the string
                sequence[iCell] += Atom.type_code.charAt(current.getType());
                // add the current atom to the list so that we will know we have seen it before
                seen.add(current);
                // move onto the next bond (we know this atom has exactly two)
                if (seen.contains((Atom) current.getBonds().getFirst()))
                    current = (Atom) current.getBonds().get(1);
                else
                    current = (Atom) current.getBonds().getFirst();
            }
            //System.out.println(sequence[iCell]);
            if (sequence[iCell].length() != 6 || sequence[iCell].charAt(0) != 'e' ||
                    sequence[iCell].charAt(5) != 'f')    //TODO add the parameter : "Incorrect template sequence detected: "+sequence[iCell];
                return getError(5);
        }
        if (sequence[0].compareTo(sequence[1]) != 0) return getError(6);

        return null;
    }
}
