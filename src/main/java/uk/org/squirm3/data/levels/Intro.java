package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

public class Intro extends Level {

    public Intro(final String title, final String challenge, final String hint,
            final String[] errors, final Configuration defaultConfiguration) {
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
        return null;
    } // TODO Is this one called even one time ???
}
