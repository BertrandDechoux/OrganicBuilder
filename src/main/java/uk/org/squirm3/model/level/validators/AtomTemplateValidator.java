package uk.org.squirm3.model.level.validators;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;

/**
 * Utilities.
 */
public abstract class AtomTemplateValidator implements AtomValidator {

	protected Optional<String> error(LevelMessages messages, int errorNumber, Stream<?> errorCases) {
		return errorCases.findAny().map(e -> messages.getError(errorNumber));
	}
	
	protected Optional<String> error(LevelMessages messages, int errorNumber, boolean error) {
		return error? Optional.of(messages.getError(errorNumber)) : Optional.empty();
	}
	
	protected String validation(Collection<? extends Atom> atoms, LevelMessages messages, ValidationCheck... checks) {
		return Arrays.stream(checks)//
				.map(c -> c.check(atoms, messages)).filter(Optional::isPresent)//
				.findFirst().orElse(Optional.empty()).orElse(null);
	}
	
	protected interface ValidationCheck {
		Optional<String> check(Collection<? extends Atom> atoms, LevelMessages messages);
	}
}
