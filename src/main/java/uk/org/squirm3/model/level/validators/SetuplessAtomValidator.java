package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomValidator;

public abstract class SetuplessAtomValidator implements AtomValidator {

    @Override
    public final void setup(final Collection<? extends Atom> atoms) {
    }

}
