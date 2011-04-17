package uk.org.squirm3.data.levels;

import java.util.LinkedList;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

public class Join_all extends Level {

    public Join_all(final String title, final String challenge,
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
        // all joined?
        final LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        if (joined.size() != atoms.length) {
            return getError(1);
        }
        return null;
    }
}
