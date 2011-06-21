package uk.org.squirm3.model.level;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.FixedPoint;

import com.google.common.collect.Lists;

public class AtomSelector {

    public static Atom findUnique(final String atomDescription,
            final Collection<? extends Atom> atoms) {
        final Collection<? extends Atom> targets = findAll(atomDescription,
                atoms);
        if (targets.isEmpty()) {
            return null;
        }
        if (targets.size() == 1) {
            return targets.iterator().next();
        }
        throw new RuntimeException("There are " + targets.size()
                + " atoms matching " + atomDescription);
    }

    public static Collection<? extends Atom> findAll(
            final String atomDescription, final Collection<? extends Atom> atoms) {
        final Atom protoAtom = getProtoAtom(atomDescription);
        final Collection<Atom> targets = Lists.newArrayList();
        for (final Atom atom : atoms) {
            if (atom.getType() == protoAtom.getType()
                    && atom.getState() == protoAtom.getState()) {
                targets.add(atom);
            }
        }
        return targets;
    }

    private static Atom getProtoAtom(final String atomDescription) {
        if (atomDescription == null || atomDescription.length() != 2) {
            throw new RuntimeException(
                    "AtomDescription should be composed of 2 characters : type and state");
        }
        return new Atom(FixedPoint.ORIGIN, atomDescription.charAt(0) - 'a',
                atomDescription.charAt(1) - '0');
    }

}
