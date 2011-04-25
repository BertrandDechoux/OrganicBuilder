package uk.org.squirm3.data.levels;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class BondPrisoner extends Level {
    private Atom prisoner;

    public BondPrisoner(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "bondprisoner", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        prisoner = null;
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 8 atoms to form a loop
        final IPhysicalPoint mobilePoint = new MobilePoint();
        final int pos_x[] = {-1, 0, 1, 1, 1, 0, -1, -1};
        final int pos_y[] = {-1, -1, -1, 0, 1, 1, 1, 0};
        for (int i = 0; i < 8; i++) {
            int state;
            if (i == 0) {
                state = 3;
            } else if (i == 1) {
                state = 4;
            } else {
                state = 2;
            }
            mobilePoint.setPositionX(size * 4.0f + pos_x[i] * size * 2.0f);
            mobilePoint.setPositionY(size * 7.0f + pos_y[i] * size * 2.0f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 0, state);
        }
        for (int i = 0; i < 8; i++) {
            atoms[i].bondWith(atoms[(i + 1) % 8]);
        }
        // add the prisoner (f1)
        mobilePoint.setPositionX(size * 4.0f);
        mobilePoint.setPositionY(size * 7.0f);
        atoms[8] = new Atom(mobilePoint, 5, 1);
        // create the others atoms
        if (createAtoms(configuration.getNumberOfAtoms() - 9,
                configuration.getTypes(), 7 * size, configuration.getWidth(),
                0, configuration.getHeight(), atoms)) {
            for (final Atom atom : atoms) {
                if (atom.getType() == 5) {
                    prisoner = atom;
                    return atoms;
                }
            }
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {
        // is the 'prisoner' atom bonded with an f?
        if (prisoner.getBonds().size() == 0) {
            return getError(1);
        }
        if (((Atom) atoms[8].getBonds().getFirst()).getType() != 5) {
            return getError(2);
        }
        return null;
    }
}
