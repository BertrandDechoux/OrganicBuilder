package uk.org.squirm3.data.levels;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class MakeLadder extends Level {

    public MakeLadder(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "makeladder", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 6 atoms to form a template
        final Random PRNG = new Random(); // a prng for use when resetting atoms
        final IPhysicalPoint mobilePoint = new MobilePoint();
        for (int i = 0; i < 6; i++) {
            int type;
            if (i == 0) {
                type = 4; // 'e' at the top
            } else if (i == 5) {
                type = 5; // 'f' at the bottom
            } else {
                type = PRNG.nextInt(4); // 'a'-'d'
            }
            mobilePoint.setPositionX(size * 1.5f);
            mobilePoint.setPositionY(size * 6.0f + i * size * 2.1f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, type, 1);
            if (i > 0) {
                atoms[i].bondWith(atoms[i - 1]);
            }
        }
        if (createAtoms(configuration.getNumberOfAtoms() - 6,
                configuration.getTypes(), 2.5f * size,
                configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
            return atoms;
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {
        final LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        if (joined.size() > 12) {
            return getError(1);
        } else if (joined.size() < 12) {
            return getError(2);
        }
        // are the types matching?
        final int original_type_count[] = {0, 0, 0, 0, 0, 0}, new_type_count[] = {
                0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 6; i++) {
            original_type_count[atoms[i].getType()]++;
        }
        Iterator it = joined.iterator();
        while (it.hasNext()) {
            new_type_count[((Atom) it.next()).getType()]++;
        }
        for (int i = 0; i < 6; i++) {
            if (new_type_count[i] != original_type_count[i] * 2) {
                return getError(3);
            }
        }
        it = joined.iterator();
        while (it.hasNext()) {
            final Atom a = (Atom) it.next();
            if (a.getType() == 4 || a.getType() == 5) {
                // 'e' and 'f'
                if (a.getBonds().size() != 2) {
                    return getError(4);
                }
            } else {
                if (a.getBonds().size() != 3) {
                    return getError(5);
                }
            }
        }
        return null;
    }
}
