package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

import com.google.common.collect.Lists;

public class PassMessageValidator implements AtomValidator {

    private Atom seed;
    private final Collection<Atom> innerAtoms = Lists.newArrayList();
    private Atom extremity;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        for (final Atom atom : atoms) {
            if (atom.getType() == BasicType.C) {
                seed = atom;
                continue;
            }
            if (atom.getType() == BasicType.B) {
                if (atom.getBonds().size() == 2) {
                    innerAtoms.add(atom);
                } else {
                    extremity = atom;
                }
            }
        }

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        if (seed.getBonds().size() != 1 || extremity.getBonds().size() != 1) {
            return messages.getError(1);
        }

        for (final Atom atom : innerAtoms) {
            if (atom.getBonds().size() != 2) {
                return messages.getError(1);
            }
        }

        if (seed.getType() != BasicType.C || extremity.getType() != BasicType.C) {
            return messages.getError(2);
        }

        for (final Atom atom : innerAtoms) {
            if (atom.getType() != BasicType.C) {
                return messages.getError(2);
            }
        }

        return null;
    }
}
