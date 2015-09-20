package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

import com.google.common.collect.Lists;

public class BreakMoleculeValidator implements AtomValidator {
	private final BasicType moleculeTopType;
	private final BasicType moleculeBottomType;
	private final int moleculeState;
	
	private final Collection<Atom> extremities;
    private final Collection<Atom> innerMolecule;
    private final Collection<Atom> gate;
	
	public BreakMoleculeValidator(BasicType moleculeTopType, BasicType moleculeBottomType, int moleculeState) {
		this.moleculeTopType = moleculeTopType;
		this.moleculeBottomType = moleculeBottomType;
		this.moleculeState = moleculeState;
		extremities = Lists.newArrayList();
		innerMolecule = Lists.newArrayList();
		gate = Lists.newArrayList();
	}

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        setupMemory(AtomSelector.findAll(moleculeTopType, moleculeState, atoms));
        setupMemory(AtomSelector.findAll(moleculeBottomType, moleculeState, atoms));
    }

    private void setupMemory(final Collection<? extends Atom> atoms) {
        for (final Atom atom : atoms) {
            if (atom.getBonds().size() == 1) {
                extremities.add(atom);
                continue;
            }
            for (final Atom other : atom.getBonds()) {
                if (other.getType() != atom.getType()) {
                    gate.add(atom);
                    continue;
                }
            }
            innerMolecule.add(atom);
        }

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        for (final Atom atom : extremities) {
            if (atom.getBonds().size() != 1) {
                return messages.getError(1);
            }
        }

        for (final Atom atom : innerMolecule) {
            if (atom.getBonds().size() != 2) {
                return messages.getError(1);
            }
        }

        for (final Atom atom : gate) {
            if (atom.getBonds().size() != 1) {
                return messages.getError(1);
            }
        }

        return null;
    }

}
