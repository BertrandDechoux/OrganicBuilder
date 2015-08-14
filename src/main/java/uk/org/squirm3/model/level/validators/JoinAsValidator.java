package uk.org.squirm3.model.level.validators;

import static uk.org.squirm3.model.level.AtomSelector.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class JoinAsValidator extends SetuplessAtomValidator {
	private static final BasicType ALUMINIUM = BasicType.A;

	@Override
	public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		return validation(atoms, messages, this::bondedNonAluminium, this::isolatedAluminium);
	}

	private Optional<String> bondedNonAluminium(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 1, atoms.stream()//
				.filter(type(ALUMINIUM).negate().and(Atom::isBonded)));
	}

	private Optional<String> isolatedAluminium(Collection<? extends Atom> atoms, LevelMessages messages) {
		Collection<Atom> aluminiumSubset = new HashSet<>();
		atoms.stream().filter(type(ALUMINIUM))//
				.findAny().ifPresent(a -> a.getAllConnectedAtoms(aluminiumSubset));

		return error(messages, 2, atoms.stream()//
				.filter(type(ALUMINIUM).and(a -> !aluminiumSubset.contains(a))));
	}
}
