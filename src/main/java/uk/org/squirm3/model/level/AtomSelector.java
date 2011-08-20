package uk.org.squirm3.model.level;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.type.AtomType;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class AtomSelector {

    @SuppressWarnings("unchecked")
    public static Collection<? extends Atom> findAll(final AtomType atomType, final int state, final Collection<? extends Atom> atoms) {
        return filter(allOf(having(on(Atom.class).getType(), is(atomType)),having(on(Atom.class).getState(), is(state))) , atoms);
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
                + " atoms matching " + atomType.getCharacterIdentifier() + state);
    }

}
