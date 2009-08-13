package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

import java.util.LinkedList;

/**
 * ${my.copyright}
 */
public class Join_all extends Level {

    public Join_all(String title, String challenge, String hint, String[] errors,
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
        // all joined?
        LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        if (joined.size() != atoms.length) return getError(1);
        return null;
    }
}
