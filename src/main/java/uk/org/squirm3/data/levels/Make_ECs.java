package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

/**
 * ${my.copyright}
 */
public class Make_ECs extends Level {

    public Make_ECs(String title, String challenge, String hint, String[] errors,
                    Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        if (createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms))
            return atoms;
        else return null;
    }

    public String evaluate(Atom[] atoms) {
        // each atom must be either 'e' and bonded to just a 'c' (or vice versa), or unbonded
        int ec_pairs_found = 0, loose_e_atoms_found = 0, loose_c_atoms_found = 0;
        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
            if (atom.getType() != 2 && atom.getType() != 4 && atom.getBonds().size() != 0) return getError(1);
            if (atom.getType() == 2 || atom.getType() == 4) {
                if (atom.getBonds().size() > 1) return getError(2);
                if (atom.getBonds().size() == 0) {
                    if (atom.getType() == 2) loose_c_atoms_found++;
                    else loose_e_atoms_found++;
                }
            }
        }
        if (Math.min(loose_c_atoms_found, loose_e_atoms_found) > 0) return getError(3);
        return null;
    }
}
