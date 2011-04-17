package uk.org.squirm3.data.levels;

import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

public class Join_same extends Level {

    public Join_same(final String title, final String challenge,
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
        for (int i = 0; i < atoms.length; i++) {
            // get everything that's joined to this atom
            final LinkedList joined = new LinkedList();
            final Atom atom = atoms[i];
            atom.getAllConnectedAtoms(joined);
            // is there any atom in this list of a different type?
            final Iterator it = joined.iterator();
            while (it.hasNext()) {
                final Atom other = (Atom) it.next();
                if (other.getType() != atom.getType()) {
                    return getError(1);
                }
            }
            // are there any atoms of the same type not on this list?
            for (int j = 0; j < atoms.length; j++) {
                if (atoms[j].getType() == atom.getType()
                        && !joined.contains(atoms[j])) {
                    return getError(2);
                }
            }
        }
        return null;
    }
}
