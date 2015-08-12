package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class LineCsValidator implements AtomValidator {
    private Atom seed;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        seed = AtomSelector.findUnique(BasicType.C, 1, atoms);
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        int single_bonded_atoms_found = 0;
        // get the set of atoms joined to atom[0]
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        seed.getAllConnectedAtoms(joined);
        // fail on bonds<1 or >2, or not in 'joined' list
        for (final Atom atom2 : atoms) {
            final Atom atom = atom2;
            if (atom.getType() != BasicType.C) {
                if (atom.getBonds().size() != 0) {
                    return messages.getError(1);
                }
                continue; // no other tests for non-'c' atoms
            }
            if (atom.getBonds().size() == 1) {
                single_bonded_atoms_found++;
            } else if (atom.getBonds().size() == 2) {
            	// do nothing
            } else if (atom.getBonds().size() == 0) {
                return messages.getError(2);
            } else {
                return messages.getError(3);
            }
            if (!joined.contains(atom)) {
                return messages.getError(4);
            }
        }
        // one final check on chain configuration
        if (single_bonded_atoms_found != 2) {
            return messages.getError(5);
        }
        return null;
    }
}
