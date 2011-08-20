package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.def.BasicType;

public class MakeLadderValidator implements AtomValidator {

    private Atom chainStart;
    private final Map<AtomType, Integer> atomTypeCount = new HashMap<AtomType, Integer>();

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        final Collection<? extends Atom> potentialStarts = AtomSelector
                .findAll(BasicType.E, 1, atoms);
        for (final Atom atom : potentialStarts) {
            if (atom.getBonds().size() == 1) {
                chainStart = atom;
                break;
            }
        }

        Atom atom = chainStart;
        for (int i = 0; i < 6; i++) {
            atom = chainStart.getBonds().getFirst();
            if (atomTypeCount.containsKey(atom.getType())) {
                atomTypeCount.put(atom.getType(),
                        atomTypeCount.get(atom.getType()) + 1);
            } else {
                atomTypeCount.put(atom.getType(), 1);
            }
        }

    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        chainStart.getAllConnectedAtoms(joined);
        if (joined.size() > 12) {
            return messages.getError(1);
        } else if (joined.size() < 12) {
            return messages.getError(2);
        }
        // are the types matching?
        final Map<AtomType, Integer> newAtomTypeCount = new HashMap<AtomType, Integer>();
        Iterator<Atom> it = joined.iterator();
        while (it.hasNext()) {
            Atom atom = it.next();
            if (atomTypeCount.containsKey(atom.getType())) {
                atomTypeCount.put(atom.getType(),
                        atomTypeCount.get(atom.getType()) + 1);
            } else {
                atomTypeCount.put(atom.getType(), 1);
            }
        }
        for (Entry<AtomType,Integer> entry : atomTypeCount.entrySet()) {
            if(!newAtomTypeCount.containsKey(entry.getKey()) ||  newAtomTypeCount.get(entry.getKey()) != entry.getValue() * 2) {
                return messages.getError(3);
            }
            
        }
        it = joined.iterator();
        while (it.hasNext()) {
            final Atom a = it.next();
            if (a.getType() == BasicType.E || a.getType() == BasicType.F) {
                // 'e' and 'f'
                if (a.getBonds().size() != 2) {
                    return messages.getError(4);
                }
            } else {
                if (a.getBonds().size() != 3) {
                    return messages.getError(5);
                }
            }
        }
        return null;
    }

}
