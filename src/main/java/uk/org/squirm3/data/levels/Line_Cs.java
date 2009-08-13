package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

import java.util.LinkedList;

/**
 * ${my.copyright}
 */
public class Line_Cs extends Level {
    private Atom seed;

    public Line_Cs(String title, String challenge, String hint, String[] errors,
                   Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        if (createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
            for (int i = 0; i < atoms.length; i++) {
                if (atoms[i].getType() == 2) {
                    seed = atoms[i];
                    seed.setState(1);
                    return atoms;
                }
            }
        }
        return null;
    }

    public String evaluate(Atom[] atoms) {
        int single_bonded_atoms_found = 0, double_bonded_atoms_found = 0;
        // get the set of atoms joined to atom[0]
        LinkedList joined = new LinkedList();
        seed.getAllConnectedAtoms(joined);
        // fail on bonds<1 or >2, or not in 'joined' list
        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
            if (atom.getType() != 2) {
                if (atom.getBonds().size() != 0) return getError(1);
                continue; // no other tests for non-'c' atoms
            }
            if (atom.getBonds().size() == 1) single_bonded_atoms_found++;
            else if (atom.getBonds().size() == 2) double_bonded_atoms_found++;
            else if (atom.getBonds().size() == 0) return getError(2);
            else return getError(3);
            if (!joined.contains(atom)) return getError(4);
        }
        // one final check on chain configuration
        if (single_bonded_atoms_found != 2) return getError(5);
        return null;
    }
}
