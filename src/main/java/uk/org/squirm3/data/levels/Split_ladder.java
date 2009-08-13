package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.*;

import java.util.Random;

/**
 * ${my.copyright}
 */
public class Split_ladder extends Level {

    public Split_ladder(String title, String challenge, String hint, String[] errors,
                        Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 20 atoms to form a template
        Random PRNG = new Random(); // a prng for use when resetting atoms
        IPhysicalPoint mobilePoint = new MobilePoint();
        for (int i = 0; i < 20; i++) {
            final int state = (i == 0 || i == 10) ? 2 : 1;
            final int type = (i < 10) ? (PRNG.nextInt(6)) : atoms[i - 10].getType();
            final float x = (i < 10) ? size * 1.5f : size * 3.7f;
            final float y = (i < 10) ? (size * 3 + i * size * 2.1f) : (size * 3 + (i - 10) * size * 2.1f);
            mobilePoint.setPositionX(x);
            mobilePoint.setPositionY(y);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, type, state);
            if (i != 0 && i != 10)
                atoms[i].bondWith(atoms[i - 1]);
            if (i >= 10)
                atoms[i].bondWith(atoms[i - 10]);
        }
        if (createAtoms(configuration.getNumberOfAtoms() - 20, configuration.getTypes(), 4.5f * size, configuration.getWidth(), 0, configuration.getHeight(), atoms))
            return atoms;
        return null;
    }

    public String evaluate(Atom[] atoms) {
        int n_bonds[] = {1, 2, 2, 2, 2, 2, 2, 2, 2, 1};
        for (int i = 0; i < 20; i++)
            if (atoms[i].getBonds().size() != n_bonds[i % 10]) return getError(1);
        return null;
    }
}
