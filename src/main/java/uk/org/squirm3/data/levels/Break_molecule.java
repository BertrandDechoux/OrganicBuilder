package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.*;

/**
 * ${my.copyright}
 */
public class Break_molecule extends Level {

    public Break_molecule(String title, String challenge, String hint, String[] errors,
                          Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 10 atoms to form a template
        IPhysicalPoint mobilePoint = new MobilePoint();
        for (int i = 0; i < 10; i++) {
            final int type = (i < 5) ? 0 : 3;
            mobilePoint.setPositionX(size * 1.5f);
            mobilePoint.setPositionY(size * 1.5f + i * size * 2.1f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, type, 1);
            if (i > 0)
                atoms[i].bondWith(atoms[i - 1]);
        }
        if (createAtoms(configuration.getNumberOfAtoms() - 10, configuration.getTypes(), 2.5f * size, configuration.getWidth(), 0, configuration.getHeight(), atoms))
            return atoms;
        return null;
    }

    public String evaluate(Atom[] atoms) {
        int n_bonds[] = {1, 2, 2, 2, 1, 1, 2, 2, 2, 1};
        for (int i = 0; i < 10; i++)
            if (atoms[i].getBonds().size() != n_bonds[i]) return getError(1);
        return null;
    }
}
