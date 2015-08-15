package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;

public class JoinAllValidator extends SetuplessAtomValidator {

	@Override
	public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		final Set<Atom> connectedAtoms = new HashSet<Atom>();
		atoms.stream().findAny().ifPresent(a -> a.getAllConnectedAtoms(connectedAtoms));
		return error(messages, 1, connectedAtoms.size() != atoms.size()).orElse(null);
	}
}
