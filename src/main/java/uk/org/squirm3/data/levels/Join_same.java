package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * ${my.copyright}
 */
public class Join_same extends Level {

    public Join_same(String title, String challenge, String hint, String[] errors,
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
        for (int i = 0; i < atoms.length; i++) {
            // get everything that's joined to this atom
            LinkedList joined = new LinkedList();
            Atom atom = atoms[i];
            atom.getAllConnectedAtoms(joined);
            // is there any atom in this list of a different type?
            Iterator it = joined.iterator();
            while (it.hasNext()) {
                Atom other = (Atom) it.next();
                if (other.getType() != atom.getType()) return getError(1);
            }
            // are there any atoms of the same type not on this list?
            for (int j = 0; j < atoms.length; j++)
                if (atoms[j].getType() == atom.getType() && !joined.contains(atoms[j]))
                    return getError(2);
        }
        return null;
    }
}
