package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;

public class InsertAtomValidator implements AtomValidator {

    private Atom chainStart;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        chainStart = AtomSelector.findUnique("a4", atoms);
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        chainStart.getAllConnectedAtoms(joined);
        if (joined.size() != 11) {
            return messages.getError(1);
        }
        final int n_bonds[] = {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1};
        final int types[] = {4, 4, 4, 4, 4, 1, 4, 4, 4, 4, 4};
        int i = 0;
        final Iterator<Atom> it = joined.iterator();
        while (it.hasNext()) {
            final Atom a = it.next();
            if (a.getBonds().size() != n_bonds[i]) {
                return messages.getError(2);
            }
            if (a.getType() != types[i]) {
                return messages.getError(3);
            }
            i++;
        }
        return null;
    }

}
