package uk.org.squirm3.data.levels;

import java.util.LinkedList;
import java.util.Random;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class Selfrep extends Level {

    public Selfrep(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "selfrep", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        final Random PRNG = new Random(); // a prng for use when resetting atoms
        // (ensure each type is used once (to allow multiple copies to be made))
        final int[][] genomes = {{0, 1, 3, 2}, {3, 1, 2, 0}, {2, 3, 0, 1},
                {1, 2, 0, 3}, {0, 3, 2, 1}};
        final int which_genome = PRNG.nextInt(5);
        final IPhysicalPoint mobilePoint = new MobilePoint();
        for (int i = 0; i < 6; i++) {
            final int type;
            if (i == 0) {
                type = 4; // 'e' at the top
            } else if (i == 5) {
                type = 5; // 'f' at the bottom
            } else {
                type = genomes[which_genome][i - 1];
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
    public String evaluate(final Atom[] atoms) { // improved code from Ralph
                                                 // Hartley
        final LinkedList joined = new LinkedList();
        // there should be at least two bonded 'e' atoms in the world, each at
        // the head of a copy
        int n_found = 0;
        int bound_atoms = 0;
        for (final Atom first : atoms) { // include the original
            if (first.getBonds().size() > 0) {
                bound_atoms++;

                if (first.getType() == 4) {
                    joined.clear();
                    first.getAllConnectedAtoms(joined);

                    if (joined.size() != 6) {
                        return getError(1);
                    }

                    final Atom last = (Atom) joined.getLast();
                    if (first.getBonds().size() != 1
                            || last.getBonds().size() != 1
                            || last.getType() != 5) {
                        return getError(2);
                    }

                    for (int j = 1; j < joined.size() - 1; j++) {
                        final Atom a = (Atom) joined.get(j);
                        if (a.getBonds().size() != 2) {
                            return getError(3);
                        }
                        if (a.getType() != atoms[j].getType()) {
                            return getError(4);
                        }
                    }
                    n_found++;
                }
            }
        }

        if (n_found == 0) {
            return getError(5);
        }
        if (n_found < 2) {
            return getError(6);
        }
        if (bound_atoms != 6 * n_found) {
            return getError(7);
        }
        return null;
    }
}
