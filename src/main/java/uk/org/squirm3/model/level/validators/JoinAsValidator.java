package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;

public class JoinAsValidator extends SetuplessAtomValidator {

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        // is any non-'a' atom bonded with any other?
        for (final Atom atom : atoms) {
            if (atom.getType() != 0 && atom.getBonds().size() > 0) {
                return messages.getError(1);
            }
        }
        // is every 'a' atom bonded together in a big clump?
        final LinkedList<Atom> a_atoms = new LinkedList<Atom>();
        for (final Atom atom : atoms) {
            if (atom.getType() == 0) {
                // this will do as our starting point
                atom.getAllConnectedAtoms(a_atoms);
                break;
            }
        }
        for (final Atom atom : atoms) {
            if (atom.getType() == 0 && !a_atoms.contains(atom)) {
                return messages.getError(2);
            }
        }
        return null;
    }

}
