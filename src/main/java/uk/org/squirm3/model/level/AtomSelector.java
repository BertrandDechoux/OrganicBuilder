package uk.org.squirm3.model.level;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.type.AtomType;

public class AtomSelector {

	public static Collection<? extends Atom> findAll(AtomType atomType, int state, Collection<? extends Atom> atoms) {
		return atoms.stream()//
				.filter(type(atomType).and(state(state)))//
				.collect(Collectors.toList());
	}

	public static Atom findUnique(AtomType atomType, int state, Collection<? extends Atom> atoms) {
		Collection<? extends Atom> targets = findAll(atomType, state, atoms);
		if (targets.isEmpty()) {
			return null;
		}
		if (targets.size() == 1) {
			return targets.iterator().next();
		}
		throw new RuntimeException(
				"There are " + targets.size() + " atoms matching " + atomType.getCharacterIdentifier() + state);
	}
	
	public static Predicate<Atom> type(AtomType type) {
		return a -> a.getType().equals(type);
	}

	public static Predicate<Atom> types(AtomType... types) {
		List<AtomType> candidates = Arrays.asList(types);
		return a -> candidates.contains(a.getType());
	}

	public static Predicate<Atom> state(int state) {
		return a -> a.getState() == state;
	}

}
