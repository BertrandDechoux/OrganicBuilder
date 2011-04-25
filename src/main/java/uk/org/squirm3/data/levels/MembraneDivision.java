package uk.org.squirm3.data.levels;

import java.util.LinkedList;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class MembraneDivision extends Level {

    public MembraneDivision(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "membranedivision", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        final IPhysicalPoint mobilePoint = new MobilePoint();
        // place and bond N atoms to form a loop
        final int N = 12;
        final int pos_y[] = {-1, 0, 1, 2, 3, 3, 3, 2, 1, 0, -1, -1}; // reading
        // clockwise
        // from the
        // top-left
        // corner (y is
        // down)
        final int pos_x[] = {-1, -1, -1, -1, -1, 0, 1, 1, 1, 1, 1, 0};
        for (int i = 0; i < N; i++) {
            int state;
            if (i == 0) {
                state = 3;
            } else if (i == N / 2) {
                state = 4;
            } else {
                state = 2;
            }
            mobilePoint.setPositionX(size * 4.0f + pos_x[i] * size * 2.0f);
            mobilePoint.setPositionY(size * 7.0f + pos_y[i] * size * 2.0f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 0, state);
        }
        for (int j = 0; j < N; j++) {
            atoms[j].bondWith(atoms[(j + 1) % N]);
        }
        // create the others atoms
        if (createAtoms(configuration.getNumberOfAtoms() - N,
                configuration.getTypes(), 7 * size, configuration.getWidth(),
                0, configuration.getHeight(), atoms)) {
            return atoms;
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {

        final int N = 12; // original loop size (see setup code, above)
        // starting from atom 0 there should be a neat closed loop of a atoms
        final LinkedList loop[] = {new LinkedList(), new LinkedList()};
        atoms[0].getAllConnectedAtoms(loop[0]);
        if (loop[0].size() >= N) {
            return getError(1);
        }
        // and there should be a second loop of 'a' atoms made of the same atoms
        for (int i = 0; i < N; i++) {
            final Atom a = atoms[i];
            if (!loop[0].contains(a)) {
                a.getAllConnectedAtoms(loop[1]);
            }
        }
        if (loop[0].size() + loop[1].size() != N) {
            return getError(2);
        }
        // each atom in each group should of type 'a' and have exactly two bonds
        // (hence a neat loop)
        for (int iLoop = 0; iLoop < 2; iLoop++) {
            for (int i = 0; i < loop[iLoop].size(); i++) {
                final Atom a = (Atom) loop[iLoop].get(i);
                if (a.getType() != 0 || a.getBonds().size() != 2) {
                    return getError(3);
                }
            }
        }
        return null;
    }
}
