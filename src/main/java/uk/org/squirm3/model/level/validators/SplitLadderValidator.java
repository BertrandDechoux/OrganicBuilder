package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

import com.google.common.collect.Lists;

public class SplitLadderValidator implements AtomValidator {

    private final Collection<Atom> extremities = Lists.newArrayList();
    private final Collection<Atom> otherAtoms = Lists.newArrayList();

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        for (final Atom atom : atoms) {
            if (atom.getType() != BasicType.B && atom.getType() != BasicType.C) {
                continue;
            }
            if (atom.getBonds().size() == 2) {
                extremities.add(atom);
                continue;
            }
            otherAtoms.add(atom);
        }

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        for (final Atom atom : extremities) {
            if (atom.getBonds().size() != 1) {
                return messages.getError(1);
            }
        }

        for (final Atom atom : otherAtoms) {
            if (atom.getBonds().size() != 2) {
                return messages.getError(1);
            }
        }

        return null;
    }

}
