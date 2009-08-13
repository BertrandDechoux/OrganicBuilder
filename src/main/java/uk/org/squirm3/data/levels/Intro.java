package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

/**
 * ${my.copyright}
 */

public class Intro extends Level {

    public Intro(String title, String challenge, String hint, String[] errors,
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
        return null;
    } // TODO Is this one called even one time ???
}
