package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;

public class AbcdefChainsValidator extends SetuplessAtomValidator {

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        // how many abcdef chains are there?
        int num_abcdef_chains_found = 0;
        for (final Atom a : atoms) {
            if (a.getType() == 0 && a.getBonds().size() == 1) {
                // looks promising - let's check
                final LinkedList<Atom> joined = new LinkedList<Atom>();
                a.getAllConnectedAtoms(joined);
                if (joined.size() != 6) {
                    continue;
                }
                final Iterator<Atom> it = joined.iterator();
                if (it.next().getType() == 0 && it.next().getType() == 1
                        && it.next().getType() == 2 && it.next().getType() == 3
                        && it.next().getType() == 4 && it.next().getType() == 5) {
                    num_abcdef_chains_found++;
                    // (this isn't a perfect test but hopefully close enough)
                }
            }
        }
        if (num_abcdef_chains_found == 0) {
            return messages.getError(1);
        } else if (num_abcdef_chains_found == 1) {
            return messages.getError(2);
        }
        return null;
    }
}
