package uk.org.squirm3.model.level.validators;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;

import com.google.common.collect.Lists;

public class BreakMoleculeValidator implements AtomValidator {
    private final Collection<Atom> extremities = Lists.newArrayList();
    private final Collection<Atom> innerMolecule = Lists.newArrayList();
    private final Collection<Atom> gate = Lists.newArrayList();

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        setupMemory(AtomSelector.findAll("a1", atoms));
        setupMemory(AtomSelector.findAll("d1", atoms));
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
