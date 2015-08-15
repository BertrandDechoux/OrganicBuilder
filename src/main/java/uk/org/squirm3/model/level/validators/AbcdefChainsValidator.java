package uk.org.squirm3.model.level.validators;

import static uk.org.squirm3.model.type.def.BasicType.A;
import static uk.org.squirm3.model.type.def.BasicType.B;
import static uk.org.squirm3.model.type.def.BasicType.C;
import static uk.org.squirm3.model.type.def.BasicType.D;
import static uk.org.squirm3.model.type.def.BasicType.E;
import static uk.org.squirm3.model.type.def.BasicType.F;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class AbcdefChainsValidator extends SetuplessAtomValidator {
	protected static final List<BasicType> CHAIN_TYPES = Arrays.asList(A, B, C, D, E, F);

	@Override
	public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		long numberOfValidChains = numberOfValidChains(atoms);
		if (numberOfValidChains == 0) {
			return messages.getError(1);
		} else if (numberOfValidChains == 1) {
			return messages.getError(2);
		}
		return null;
	}

	private long numberOfValidChains(Collection<? extends Atom> atoms) {
		return atoms.stream().filter(this::startsValidChain).count();
	}

	private boolean startsValidChain(Atom start) {
		if (isValidStart(start)) {
			final List<Atom> connectedAtoms = new LinkedList<Atom>();
			start.getAllConnectedAtoms(connectedAtoms);
			if (connectedAtoms.size() != CHAIN_TYPES.size()) {
				return false;
			}
			for (int i = 0; i < CHAIN_TYPES.size(); i++) {
				Atom atom = connectedAtoms.get(i);
				if (!atom.getType().equals(CHAIN_TYPES.get(i))) {
					return false;
				}
				if (atom.getBonds().size() != bondSizeAt(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isValidStart(Atom start) {
		return start.getType().equals(CHAIN_TYPES.get(0)) && start.getBonds().size() == 1;
	}

	private int bondSizeAt(int index) {
		return (index == 0 || index == CHAIN_TYPES.size() - 1) ? 1 : 2;
	}
}
