package uk.org.squirm3.data.levels;

import java.util.LinkedList;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

public class Join_As extends Level {

    public Join_As(final String title, final String challenge,
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
        // is any non-'a' atom bonded with any other?
        for (final Atom atom : atoms) {
            if (atom.getType() != 0 && atom.getBonds().size() > 0) {
                return getError(1);
            }
        }
        // is every 'a' atom bonded together in a big clump?
        final LinkedList a_atoms = new LinkedList();
        for (final Atom atom : atoms) {
            if (atom.getType() == 0) {
                // this will do as our starting point
                atom.getAllConnectedAtoms(a_atoms);
                break;
            }
        }
        for (int i = 0; i < atoms.length; i++) {
            if (atoms[i].getType() == 0 && !a_atoms.contains(atoms[i])) {
                return getError(2);
            }
        }
        return null;
    }
}
