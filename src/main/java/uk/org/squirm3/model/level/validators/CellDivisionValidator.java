package uk.org.squirm3.model.level.validators;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class CellDivisionValidator implements AtomValidator {

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        // TODO Auto-generated method stub

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {

        // evaluation of this level is quite tricky. we could be strict and
        // insist on neat membranes but that is
        // not really the intention.

        // pseudocode: non-embedded connected components, identification of the
        // copies, check for template copy

        final List<List<Atom>> components = new LinkedList<List<Atom>>();
        // stores a list of LinkedList's, the components
        for (final Atom a : atoms) {
            // is this atom already in a connected component?
            boolean already_seen = false;
            for (int iComponent = 0; iComponent < components.size(); iComponent++) {
                if (components.get(iComponent).contains(a)) {
                    already_seen = true;
                    break;
                }
            }
            if (already_seen) {
                continue;
            }
            // create a new connected component starting from this atom
            final LinkedList<Atom> component = new LinkedList<Atom>();
            a.getAllConnectedAtoms(component);
            if (component.size() > 6) {
                components.add(component);
            }
        }

        if (components.size() != 2) {
            // two large components
            return messages.getError(1);
        }
        // neither component should be inside the other
        {
            final Polygon poly[] = new Polygon[2];
            // assemble the two polygons (doesn't matter if they're a bit messy
            // at places)
            for (int iComp = 0; iComp < 2; iComp++) {
                final int NP = components.get(iComp).size();
                final int px[] = new int[NP], py[] = new int[NP];
                for (int i = 0; i < NP; i++) {
                    final Atom a = components.get(iComp).get(i);
                    px[i] = (int) a.getPhysicalPoint().getPositionX();
                    py[i] = (int) a.getPhysicalPoint().getPositionY();
                }
                poly[iComp] = new Polygon(px, py, NP);
            }
            // check for either polygon having a point inside the other
            // (given that bond-crossing is forbidden, we expect this to be a
            // complete test of separatedness)
            for (int iComp = 0; iComp < 2; iComp++) {
                final List<Atom> c = components.get(iComp);
                final int NP = c.size();
                for (int i = 0; i < NP; i++) {
                    final Atom a = c.get(i);
                    // is this point inside the other polygon?
                    if (poly[1 - iComp].contains(new Point2D.Float(a
                            .getPhysicalPoint().getPositionX(), a
                            .getPhysicalPoint().getPositionY()))) {
                        return messages.getError(2);
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
        final Iterator<? extends Atom> iterator = atoms.iterator();
        while (iterator.hasNext() && n_found < 2) {
            final Atom a = iterator.next();
            if (a.getType() == BasicType.E && a.getState() != 0
                    && a.getBonds().size() == 2) {
                heads[n_found++] = a;
            }
        }
        if (n_found < 2) {
            return messages.getError(3);
        }
        // each head should be in a separate component
        final List<Atom> c1 = components.get(0), c2 = components.get(1);
        if (c1.contains(heads[0]) && !c2.contains(heads[1])
                || c2.contains(heads[0]) && !c1.contains(heads[1])) {
            return messages.getError(4);
        }
        // work down each template, adding the type of each 2-connected atom to
        // sequence[i]
        final String sequence[] = {new String(), new String()};
        for (int iCell = 0; iCell < 2; iCell++) {
            final List<Atom> seen = new LinkedList<Atom>();
            Atom current = heads[iCell];
            seen.add(current);
            sequence[iCell] = "e"; // let's get things started
            if (current.getBonds().getFirst().getBonds().size() == 2) {
                current = current.getBonds().getFirst();
            } else {
                current = current.getBonds().getLast();
            }
            while (sequence[iCell].length() < 10) {
                // if the current atom has other than 2 bonds then we are done
                if (current.getBonds().size() != 2) {
                    break;
                }
                // append the type letter (a-f) to the string
                sequence[iCell] += current.getType().getCharacterIdentifier();
                // add the current atom to the list so that we will know we have
                // seen it before
                seen.add(current);
                // move onto the next bond (we know this atom has exactly two)
                if (seen.contains(current.getBonds().getFirst())) {
                    current = current.getBonds().get(1);
                } else {
                    current = current.getBonds().getFirst();
                }
            }
            // System.out.println(sequence[iCell]);
            if (sequence[iCell].length() != 6
                    || sequence[iCell].charAt(0) != 'e'
                    || sequence[iCell].charAt(5) != 'f') {
                // parameter :
                // "Incorrect template sequence detected: "+sequence[iCell];
                return messages.getError(5);
            }
        }
        if (sequence[0].compareTo(sequence[1]) != 0) {
            return messages.getError(6);
        }

        return null;
    }

}
