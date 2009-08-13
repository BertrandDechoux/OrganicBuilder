package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.*;

/**
 * ${my.copyright}
 */
public class Bond_prisoner extends Level {
    private Atom prisoner;

    public Bond_prisoner(String title, String challenge, String hint, String[] errors,
                         Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        prisoner = null;
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 8 atoms to form a loop
        IPhysicalPoint mobilePoint = new MobilePoint();
        int pos_x[] = {-1, 0, 1, 1, 1, 0, -1, -1};
        int pos_y[] = {-1, -1, -1, 0, 1, 1, 1, 0};
        for (int i = 0; i < 8; i++) {
            int state;
            if (i == 0) state = 3;
            else if (i == 1) state = 4;
            else state = 2;
            mobilePoint.setPositionX(size * 4.0f + pos_x[i] * size * 2.0f);
            mobilePoint.setPositionY(size * 7.0f + pos_y[i] * size * 2.0f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 0, state);
        }
        for (int i = 0; i < 8; i++) atoms[i].bondWith(atoms[(i + 1) % 8]);
        // add the prisoner (f1)
        mobilePoint.setPositionX(size * 4.0f);
        mobilePoint.setPositionY(size * 7.0f);
        atoms[8] = new Atom(mobilePoint, 5, 1);
        // create the others atoms
        if (createAtoms(configuration.getNumberOfAtoms() - 9, configuration.getTypes(), 7 * size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
            for (int i = 0; i < atoms.length; i++) {
                if (atoms[i].getType() == 5) {
                    prisoner = atoms[i];
                    return atoms;
                }
            }
        }
        return null;
    }

    public String evaluate(Atom[] atoms) {
        // is the 'prisoner' atom bonded with an f?
        if (prisoner.getBonds().size() == 0) return getError(1);
        if (((Atom) atoms[8].getBonds().getFirst()).getType() != 5) return getError(2);
        return null;
    }
}
