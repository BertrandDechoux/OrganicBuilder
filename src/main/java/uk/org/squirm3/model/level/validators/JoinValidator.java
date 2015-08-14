package uk.org.squirm3.model.level.validators;

import static uk.org.squirm3.model.level.AtomSelector.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class JoinValidator extends SetuplessAtomValidator {
	private final BasicType aluminium;
	
	public JoinValidator(BasicType aluminium) {
		this.aluminium = aluminium;
	}

	@Override
	public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		return validation(atoms, messages, this::bondedNonAluminium, this::isolatedAluminium);
	}

	private Optional<String> bondedNonAluminium(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 1, atoms.stream()//
				.filter(type(aluminium).negate().and(Atom::isBonded)));
	}

	private Optional<String> isolatedAluminium(Collection<? extends Atom> atoms, LevelMessages messages) {
		Collection<Atom> aluminiumSubset = new HashSet<>();
		atoms.stream().filter(type(aluminium))//
				.findAny().ifPresent(a -> a.getAllConnectedAtoms(aluminiumSubset));

		return error(messages, 2, atoms.stream()//
				.filter(type(aluminium).and(a -> !aluminiumSubset.contains(a))));
	}
}
