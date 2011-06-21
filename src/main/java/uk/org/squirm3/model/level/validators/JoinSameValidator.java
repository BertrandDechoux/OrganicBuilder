package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;

public class JoinSameValidator extends SetuplessAtomValidator {

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        for (final Atom atom : atoms) {
            // get everything that's joined to this atom
            final LinkedList<Atom> joined = new LinkedList<Atom>();
            atom.getAllConnectedAtoms(joined);
            // is there any atom in this list of a different type?
            final Iterator<Atom> it = joined.iterator();
            while (it.hasNext()) {
                final Atom other = it.next();
                if (other.getType() != atom.getType()) {
                    return messages.getError(1);
                }
            }
            // are there any atoms of the same type not on this list?
            for (final Atom otherAtom : atoms) {
                if (otherAtom.getType() == atom.getType()
                        && !joined.contains(otherAtom)) {
                    return messages.getError(2);
                }
            }
        }
        return null;
    }

}
