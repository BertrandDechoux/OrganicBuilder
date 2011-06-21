package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;

public class MakeLadderValidator implements AtomValidator {

    private Atom chainStart;
    private final int[] chainTypesCount = {0, 0, 0, 0, 0, 0};

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        final Collection<? extends Atom> potentialStarts = AtomSelector
                .findAll("e1", atoms);
        for (final Atom atom : potentialStarts) {
            if (atom.getBonds().size() == 1) {
                chainStart = atom;
                break;
            }
        }

        Atom atom = chainStart;
        for (int i = 0; i < 6; i++) {
            atom = chainStart.getBonds().getFirst();
            chainTypesCount[atom.getType()]++;
        }

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        chainStart.getAllConnectedAtoms(joined);
        if (joined.size() > 12) {
            return messages.getError(1);
        } else if (joined.size() < 12) {
            return messages.getError(2);
        }
        // are the types matching?
        final int[] new_type_count = {0, 0, 0, 0, 0, 0};
        Iterator<Atom> it = joined.iterator();
        while (it.hasNext()) {
            new_type_count[it.next().getType()]++;
        }
        for (int i = 0; i < 6; i++) {
            if (new_type_count[i] != chainTypesCount[i] * 2) {
                return messages.getError(3);
            }
        }
        it = joined.iterator();
        while (it.hasNext()) {
            final Atom a = it.next();
            if (a.getType() == 4 || a.getType() == 5) {
                // 'e' and 'f'
                if (a.getBonds().size() != 2) {
                    return messages.getError(4);
                }
            } else {
                if (a.getBonds().size() != 3) {
                    return messages.getError(5);
                }
            }
        }
        return null;
    }

}
