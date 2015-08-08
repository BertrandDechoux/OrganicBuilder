package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class ConnectCornersValidator implements AtomValidator {

    private Atom upperLeftCorner;
    private Atom bottomRightCorner;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        upperLeftCorner = AtomSelector.findUnique(BasicType.A, 1, atoms);
        bottomRightCorner = AtomSelector.findUnique(BasicType.F, 1, atoms);
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        upperLeftCorner.getAllConnectedAtoms(joined);
        if (!joined.contains(bottomRightCorner)) {
            return messages.getError(1);
        }
        return null;
    }
}
