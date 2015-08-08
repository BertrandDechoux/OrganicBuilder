package uk.org.squirm3.model.level;

import java.util.Collection;
import java.util.stream.Collectors;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.type.AtomType;

public class AtomSelector {

    public static Collection<? extends Atom> findAll(final AtomType atomType,
            final int state, final Collection<? extends Atom> atoms) {
        return atoms
                .stream()
                .filter(a -> a.getType().equals(atomType)
                        && a.getState() == state).collect(Collectors.toList());
    }

    public static Atom findUnique(final AtomType atomType, final int state,
            final Collection<? extends Atom> atoms) {
        final Collection<? extends Atom> targets = findAll(atomType, state,
                atoms);
        if (targets.isEmpty()) {
            return null;
        }
        if (targets.size() == 1) {
            return targets.iterator().next();
        }
        throw new RuntimeException("There are " + targets.size()
                + " atoms matching " + atomType.getCharacterIdentifier()
                + state);
    }

}
