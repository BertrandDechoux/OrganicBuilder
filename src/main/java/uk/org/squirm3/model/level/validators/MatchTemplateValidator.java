package uk.org.squirm3.model.level.validators;

import static uk.org.squirm3.model.level.AtomSelector.state;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;

public class MatchTemplateValidator extends AtomTemplateValidator {
    private final int templateState;
	private final Collection<Atom> template;

    public MatchTemplateValidator(int templateState) {
		this.templateState = templateState;
		this.template = Lists.newArrayList();
	}

	@Override
    public void setup(Collection<? extends Atom> atoms) {
		template.addAll(atoms.stream()//
				.filter(state(templateState))//
				.collect(Collectors.toList()));
		if (template.isEmpty()) {
			throw new IllegalStateException("No template found.");
		}
    }

    @Override
    public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
        for (Atom atom : template) {
            Atom a = atom;
            Atom b = a.getBonds().getLast();
            if (b.getType() != a.getType() || b.getBonds().size() != 1) {
                return messages.getError(1);
                // (not a complete test, but hopefully ok)
            }
        }

        return null;
    }
}
