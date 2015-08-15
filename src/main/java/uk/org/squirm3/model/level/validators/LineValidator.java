package uk.org.squirm3.model.level.validators;

import static uk.org.squirm3.model.level.AtomSelector.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class LineValidator extends AtomTemplateValidator {
	private final BasicType carbon;
	private final int seedState;
	private Atom seed;

	public LineValidator(BasicType carbon, int seedState) {
		this.carbon = carbon;
		this.seedState = seedState;
	}

	@Override
	public void setup(Collection<? extends Atom> atoms) {
		seed = AtomSelector.findUnique(carbon, seedState, atoms);
		if (seed == null)
			throw new IllegalStateException("Seed not found.");
	}

	@Override
	public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		return validation(atoms, messages, //
				this::bondedNonCarbon, this::looseCarbon, this::carbonStructure, this::isolatedCarbon,
				this::carbonLoop);
	}

	private Optional<String> bondedNonCarbon(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 1, atoms.stream()//
				.filter(type(carbon).negate().and(Atom::isBonded)));
	}

	private Optional<String> looseCarbon(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 2, atoms.stream()//
				.filter(type(carbon).and(Atom::isLoose)));
	}

	private Optional<String> carbonStructure(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 3, atoms.stream()//
				.filter(type(carbon).and(a -> a.getBonds().size() > 2)));
	}

	private Optional<String> isolatedCarbon(Collection<? extends Atom> atoms, LevelMessages messages) {
		Set<Atom> seedConnectedAtoms = seedConnectedAtoms();
		return error(messages, 4, atoms.stream()//
				.filter(type(carbon).and(a -> !seedConnectedAtoms.contains(a))));
	}

	private Optional<String> carbonLoop(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 5, atoms.stream()//
				.filter(type(carbon).and(a -> a.getBonds().size() == 1)).count() != 2);
	}

	private Set<Atom> seedConnectedAtoms() {
		Set<Atom> joined = new HashSet<Atom>();
		seed.getAllConnectedAtoms(joined);
		return joined;
	}
}
