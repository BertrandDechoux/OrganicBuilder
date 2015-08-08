package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

import com.google.common.collect.Lists;

public class MembraneDivisionValidator implements AtomValidator {

    private Atom loopSeed;
    private List<Atom> loop;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        loopSeed = AtomSelector.findUnique(BasicType.A, 3, atoms);
        loop = Lists.newArrayList();
        for (final Atom atom : atoms) {
            if (atom.getType() == BasicType.A) {
                loop.add(atom);
            }
        }
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        final int N = 12; // original loop size (see setup code, above)
        // starting from atom 0 there should be a neat closed loop of a atoms
        final LinkedList<Atom> loop0 = new LinkedList<Atom>();
        final LinkedList<Atom> loop1 = new LinkedList<Atom>();
        loopSeed.getAllConnectedAtoms(loop0);
        if (loop0.size() >= N) {
            return messages.getError(1);
        }
        // and there should be a second loop of 'a' atoms made of the same atoms
        for (final Atom atom : loop) {
            if (!loop0.contains(atom)) {
                atom.getAllConnectedAtoms(loop1);
            }
        }

        if (loop0.size() + loop1.size() != N) {
            return messages.getError(2);
        }
        // each atom in each group should of type 'a' and have exactly two bonds
        // (hence a neat loop)
        for (int i = 0; i < loop0.size(); i++) {
            final Atom a = loop0.get(i);
            if (a.getType() != BasicType.A || a.getBonds().size() != 2) {
                return messages.getError(3);
            }
        }

        for (int i = 0; i < loop1.size(); i++) {
            final Atom a = loop1.get(i);
            if (a.getType() != BasicType.A || a.getBonds().size() != 2) {
                return messages.getError(3);
            }
        }
        return null;
    }

}
