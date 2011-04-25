package uk.org.squirm3.data.levels;

import java.util.LinkedList;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

public class LineCs extends Level {
    private Atom seed;

    public LineCs(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "linecs", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        if (createAtoms(configuration.getNumberOfAtoms(),
                configuration.getTypes(), 0, configuration.getWidth(), 0,
                configuration.getHeight(), atoms)) {
            for (final Atom atom : atoms) {
                if (atom.getType() == 2) {
                    seed = atom;
                    seed.setState(1);
                    return atoms;
                }
            }
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {
        int single_bonded_atoms_found = 0, double_bonded_atoms_found = 0;
        // get the set of atoms joined to atom[0]
        final LinkedList joined = new LinkedList();
        seed.getAllConnectedAtoms(joined);
        // fail on bonds<1 or >2, or not in 'joined' list
        for (final Atom atom2 : atoms) {
            final Atom atom = atom2;
            if (atom.getType() != 2) {
                if (atom.getBonds().size() != 0) {
                    return getError(1);
                }
                continue; // no other tests for non-'c' atoms
            }
            if (atom.getBonds().size() == 1) {
                single_bonded_atoms_found++;
            } else if (atom.getBonds().size() == 2) {
                double_bonded_atoms_found++;
            } else if (atom.getBonds().size() == 0) {
                return getError(2);
            } else {
                return getError(3);
            }
            if (!joined.contains(atom)) {
                return getError(4);
            }
        }
        // one final check on chain configuration
        if (single_bonded_atoms_found != 2) {
            return getError(5);
        }
        return null;
    }
}
