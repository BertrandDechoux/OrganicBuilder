package uk.org.squirm3.model.level.validators;

import static uk.org.squirm3.model.level.AtomSelector.*;

import java.util.Collection;
import java.util.Optional;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class MakeECsValidator extends SetuplessAtomValidator {
    private static final BasicType EUROPIUM = BasicType.E;
	private static final BasicType CARBON = BasicType.C;

	@Override
	public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		return validation(atoms, messages, //
				this::bondedOtherAtom, this::europiumCarbonStructure, this::looseCandidatePair);
	}

	private Optional<String> looseCandidatePair(Collection<? extends Atom> atoms, LevelMessages messages) {
        long looseEuropium = countLoose(atoms, EUROPIUM);
		long looseCarbon = countLoose(atoms, CARBON);
        if (Math.min(looseEuropium, looseCarbon) > 0) {
            return Optional.of(messages.getError(3));
        }
        return Optional.empty();
	}

	private long countLoose(Collection<? extends Atom> atoms, BasicType e) {
		return atoms.stream().filter(type(e).and(Atom::isLoose)).count();
	}

	private Optional<String> bondedOtherAtom(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 1, atoms.stream()//
    			.filter(types(EUROPIUM, CARBON).negate().and(Atom::isBonded)));
	}
	
	private Optional<String> europiumCarbonStructure(Collection<? extends Atom> atoms, LevelMessages messages) {
		return error(messages, 2, atoms.stream()//
    			.filter(types(EUROPIUM, CARBON).and(a -> a.getBonds().size() > 1)));
	}

}
