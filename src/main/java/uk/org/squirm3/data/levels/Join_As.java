package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

import java.util.LinkedList;

/**
 * ${my.copyright}
 */

public class Join_As extends Level {

    public Join_As(String title, String challenge, String hint, String[] errors,
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
        // is any non-'a' atom bonded with any other?
        for (int i = 0; i < atoms.length; i++)
            if (atoms[i].getType() != 0 && atoms[i].getBonds().size() > 0) return getError(1);
        // is every 'a' atom bonded together in a big clump?
        LinkedList a_atoms = new LinkedList();
        for (int i = 0; i < atoms.length; i++) {
            if (atoms[i].getType() == 0) {
                // this will do as our starting point
                atoms[i].getAllConnectedAtoms(a_atoms);
                break;
            }
        }
        for (int i = 0; i < atoms.length; i++)
            if (atoms[i].getType() == 0 && !a_atoms.contains(atoms[i])) return getError(2);
        return null;
    }
}
