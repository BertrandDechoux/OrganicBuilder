package uk.org.squirm3.model.level.validators;

import static uk.org.squirm3.model.level.AtomSelector.type;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class JoinSameValidator extends SetuplessAtomValidator {

    @Override
    public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		return validation(atoms, messages, this::bondedOtherType, this::isolated);
    }
    
	private Optional<String> bondedOtherType(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 1, //
				Arrays.stream(BasicType.values()).flatMap(t -> bondedOtherType(t, atoms)));
	}
	
	private Stream<? extends Atom> bondedOtherType(BasicType type, Collection<? extends Atom> atoms) {
		return atoms.stream().filter(type(type))//
				.findAny().map(a -> {
					Set<Atom> connectedAtoms = new HashSet<>();
					a.getAllConnectedAtoms(connectedAtoms);
					return connectedAtoms.stream().filter(type(type).negate());
				}).orElse(Stream.empty());
	}
    
	private Optional<String> isolated(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 2, //
				Arrays.stream(BasicType.values()).flatMap(t -> isolated(t, atoms)));
	}
	
	private Stream<? extends Atom> isolated(BasicType type, Collection<? extends Atom> atoms) {
		Collection<Atom> subset = new HashSet<>();
		atoms.stream().filter(type(type))//
				.findAny().ifPresent(a -> a.getAllConnectedAtoms(subset));
		return atoms.stream()//
				.filter(type(type).and(a -> !subset.contains(a)));
	}

}
