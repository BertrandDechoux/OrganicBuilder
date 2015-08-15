package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class ConnectValidator extends AtomTemplateValidator {
	private final BasicType upperLeftCornerType;
	private final BasicType bottomRightCornerType;
	private final int cornerState;

	private Atom upperLeftCorner;
	private Atom bottomRightCorner;

	public ConnectValidator(BasicType upperLeftCornerType, BasicType bottomRightCornerType, int cornerState) {
		this.upperLeftCornerType = upperLeftCornerType;
		this.bottomRightCornerType = bottomRightCornerType;
		this.cornerState = cornerState;
	}
	
	@Override
	public void setup(Collection<? extends Atom> atoms) {
		upperLeftCorner = AtomSelector.findUnique(upperLeftCornerType, cornerState, atoms);
		bottomRightCorner = AtomSelector.findUnique(bottomRightCornerType, cornerState, atoms);
		if (upperLeftCorner == null) {
			throw new IllegalStateException("Upper left corner not found.");
		}
		if (bottomRightCorner == null) {
			throw new IllegalStateException("Bottom right corner not found.");
		}
	}

	@Override
	public String evaluate(Collection<? extends Atom> atoms, LevelMessages messages) {
		Set<Atom> connectedAtoms = new HashSet<Atom>();
		upperLeftCorner.getAllConnectedAtoms(connectedAtoms);
		return error(messages, 1, !connectedAtoms.contains(bottomRightCorner)).orElse(null);
	}
}
