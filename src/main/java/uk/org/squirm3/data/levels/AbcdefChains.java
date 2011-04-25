package uk.org.squirm3.data.levels;

import java.util.Iterator;
import java.util.LinkedList;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

public class AbcdefChains extends Level {

    public AbcdefChains(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "abcdefchains", defaultConfiguration);
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
        // how many abcdef chains are there?
        int num_abcdef_chains_found = 0;
        for (final Atom a : atoms) {
            if (a.getType() == 0 && a.getBonds().size() == 1) {
                // looks promising - let's check
                final LinkedList joined = new LinkedList();
                a.getAllConnectedAtoms(joined);
                if (joined.size() != 6) {
                    continue;
                }
                final Iterator it = joined.iterator();
                if (((Atom) it.next()).getType() == 0
                        && ((Atom) it.next()).getType() == 1
                        && ((Atom) it.next()).getType() == 2
                        && ((Atom) it.next()).getType() == 3
                        && ((Atom) it.next()).getType() == 4
                        && ((Atom) it.next()).getType() == 5) {
                    num_abcdef_chains_found++;
                    // (this isn't a perfect test but hopefully close enough)
                }
            }
        }
        if (num_abcdef_chains_found == 0) {
            return getError(1);
        } else if (num_abcdef_chains_found == 1) {
            return getError(2);
        }
        return null;
    }
}
