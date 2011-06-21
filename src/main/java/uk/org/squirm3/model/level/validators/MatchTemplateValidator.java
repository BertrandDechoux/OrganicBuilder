package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;

import com.google.common.collect.Lists;

public class MatchTemplateValidator implements AtomValidator {

    private final Collection<Atom> template = Lists.newArrayList();

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        for (final Atom atom : atoms) {
            if (atom.getState() == 1) {
                template.add(atom);
            }
        }

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        for (final Atom atom : template) {
            final Atom a = atom;
            final Atom b = a.getBonds().getLast();
            if (b.getType() != a.getType() || b.getBonds().size() != 1) {
                return messages.getError(1);
                // (not a complete test, but hopefully ok)
            }
        }

        return null;
    }

}
