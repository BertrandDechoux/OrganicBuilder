package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

public class Make_ECs extends Level {

    public Make_ECs(final String title, final String challenge,
            final String hint, final String[] errors,
            final Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        if (createAtoms(configuration.getNumberOfAtoms(),
                configuration.getTypes(), 0, configuration.getWidth(), 0,
                configuration.getHeight(), atoms)) {
            return atoms;
        } else {
            return null;
        }
    }

    @Override
    public String evaluate(final Atom[] atoms) {
        int loose_e_atoms_found = 0, loose_c_atoms_found = 0;
        for (final Atom atom : atoms) {
            if (atom.getType() != 2 && atom.getType() != 4
                    && atom.getBonds().size() != 0) {
                return getError(1);
            }
            if (atom.getType() == 2 || atom.getType() == 4) {
                if (atom.getBonds().size() > 1) {
                    return getError(2);
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
            return getError(3);
        }
        return null;
    }
}
