package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;

public class BondPrisonerValidator implements AtomValidator {
    private Atom prisoner;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        prisoner = AtomSelector.findUnique("f1", atoms);
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        // is the 'prisoner' atom bonded with an f?
        if (prisoner.getBonds().size() == 0) {
            return messages.getError(1);
        }
        if (prisoner.getBonds().getFirst().getType() != 5) {
            return messages.getError(2);
        }
        return null;
    }
}
