package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.*;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * ${my.copyright}
 */
public class Insert_atom extends Level {

    public Insert_atom(String title, String challenge, String hint, String[] errors,
                       Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 10 atoms to form a template
        IPhysicalPoint mobilePoint = new MobilePoint();
        for (int i = 0; i < 10; i++) {
            final int state;
            if (i == 4) state = 2;
            else if (i == 5) state = 3;
            else state = 1;
            mobilePoint.setPositionX(size * 1.5f);
            mobilePoint.setPositionY(size * 3.0f + i * size * 2.1f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 4, state);
            if (i > 0)
                atoms[i].bondWith(atoms[i - 1]);
        }
        if (createAtoms(configuration.getNumberOfAtoms() - 10, configuration.getTypes(), 2.5f * size, configuration.getWidth(), 0, configuration.getHeight(), atoms))
            return atoms;
        return null;

    }

    public String evaluate(Atom[] atoms) {
        LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        if (joined.size() != 11) return getError(1);
        int n_bonds[] = {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1};
        int types[] = {4, 4, 4, 4, 4, 1, 4, 4, 4, 4, 4};
        int i = 0;
        Iterator it = joined.iterator();
        while (it.hasNext()) {
            Atom a = (Atom) it.next();
            if (a.getBonds().size() != n_bonds[i]) return getError(2);
            if (a.getType() != types[i]) return getError(3);
            i++;
        }
        return null;
    }
}
