package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.def.BasicType;

public class SelfrepValidator implements AtomValidator {

    private Atom chainStart;
    private final AtomType[] chainTypes = {BasicType.A, BasicType.A, BasicType.A, BasicType.A};

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        final Collection<? extends Atom> potentialStarts = AtomSelector
                .findAll(BasicType.E, 1, atoms);
        for (final Atom atom : potentialStarts) {
            if (atom.getBonds().size() == 1) {
                chainStart = atom;
                break;
            }
        }

        Atom atom = chainStart;
        for (int i = 0; i < 4; i++) {
            atom = chainStart.getBonds().getFirst();
            chainTypes[i] = atom.getType();
        }

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) { // improved code from Ralph
        // Hartley
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        // there should be at least two bonded 'e' atoms in the world, each at
        // the head of a copy
        int n_found = 0;
        int bound_atoms = 0;
        for (final Atom first : atoms) { // include the original
            if (first.getBonds().size() > 0) {
                bound_atoms++;

                if (first.getType() == BasicType.E) {
                    joined.clear();
                    first.getAllConnectedAtoms(joined);

                    if (joined.size() != 6) {
                        return messages.getError(1);
                    }

                    final Atom last = joined.getLast();
                    if (first.getBonds().size() != 1
                            || last.getBonds().size() != 1
                            || last.getType() != BasicType.F) {
                        return messages.getError(2);
                    }

                    for (int j = 1; j < joined.size() - 1; j++) {
                        final Atom a = joined.get(j);
                        if (a.getBonds().size() != 2) {
                            return messages.getError(3);
                        }
                        if (a.getType() != chainTypes[j - 1]) {
                            return messages.getError(4);
                        }
                    }
                    n_found++;
                }
            }
        }

        if (n_found == 0) {
            return messages.getError(5);
        }
        if (n_found < 2) {
            return messages.getError(6);
        }
        if (bound_atoms != 6 * n_found) {
            return messages.getError(7);
        }
        return null;
    }
}
