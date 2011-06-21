package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;

public class MakeECsValidator extends SetuplessAtomValidator {

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        int loose_e_atoms_found = 0, loose_c_atoms_found = 0;
        for (final Atom atom : atoms) {
            if (atom.getType() != 2 && atom.getType() != 4
                    && atom.getBonds().size() != 0) {
                return messages.getError(1);
            }
            if (atom.getType() == 2 || atom.getType() == 4) {
                if (atom.getBonds().size() > 1) {
                    return messages.getError(2);
                }
                if (atom.getBonds().size() == 0) {
                    if (atom.getType() == 2) {
                        loose_c_atoms_found++;
                    } else {
                        loose_e_atoms_found++;
                    }
                }
            }
        }
        if (Math.min(loose_c_atoms_found, loose_e_atoms_found) > 0) {
            return messages.getError(3);
        }
        return null;
    }

}
