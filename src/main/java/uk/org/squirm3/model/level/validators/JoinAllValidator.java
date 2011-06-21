package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;

public class JoinAllValidator extends SetuplessAtomValidator {

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        // all joined?
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        atoms.iterator().next().getAllConnectedAtoms(joined);
        if (joined.size() != atoms.size()) {
            return messages.getError(1);
        }
        return null;
    }
}
