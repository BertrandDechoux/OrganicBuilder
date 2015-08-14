package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;

/**
 * When no setup is required.
 */
public abstract class SetuplessAtomValidator extends AtomTemplateValidator {

    @Override
    public final void setup(final Collection<? extends Atom> atoms) {
    }

}
