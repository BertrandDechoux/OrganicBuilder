package uk.org.squirm3.data.levels;

import java.util.Random;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class Pass_message extends Level {

    public Pass_message(final String title, final String challenge,
            final String hint, final String[] errors,
            final Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 10 atoms to form a template
        final Random PRNG = new Random(); // a prng for use when resetting atoms
        final IPhysicalPoint mobilePoint = new MobilePoint();
        for (int i = 0; i < 10; i++) {
            final int state = i == 0 ? 2 : 1;
            mobilePoint.setPositionX(size * 1.5f);
            mobilePoint.setPositionY(size * 1.5f + i * size * 2.1f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, PRNG.nextInt(6), state);
            if (i > 0) {
                atoms[i].bondWith(atoms[i - 1]);
            }
        }
        if (createAtoms(configuration.getNumberOfAtoms() - 10,
                configuration.getTypes(), 2.5f * size,
                configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
            return atoms;
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {
        final int n_bonds[] = {1, 2, 2, 2, 2, 2, 2, 2, 2, 1};
        for (int i = 0; i < 10; i++) {
            if (atoms[i].getBonds().size() != n_bonds[i]) {
                return getError(1);
            } else if (atoms[i].getState() != 2) {
                return getError(2);
            }
        }
        return null;
    }
}
