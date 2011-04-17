package uk.org.squirm3.data.levels;

import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

public class Insert_atom extends Level {

    public Insert_atom(final String title, final String challenge,
            final String hint, final String[] errors,
            final Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        // place and bond 10 atoms to form a template
        final IPhysicalPoint mobilePoint = new MobilePoint();
        for (int i = 0; i < 10; i++) {
            final int state;
            if (i == 4) {
                state = 2;
            } else if (i == 5) {
                state = 3;
            } else {
                state = 1;
            }
            mobilePoint.setPositionX(size * 1.5f);
            mobilePoint.setPositionY(size * 3.0f + i * size * 2.1f);
            Level.setRandomSpeed(mobilePoint);
            atoms[i] = new Atom(mobilePoint, 4, state);
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
        final LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        if (joined.size() != 11) {
            return getError(1);
        }
        final int n_bonds[] = {1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1};
        final int types[] = {4, 4, 4, 4, 4, 1, 4, 4, 4, 4, 4};
        int i = 0;
        final Iterator it = joined.iterator();
        while (it.hasNext()) {
            final Atom a = (Atom) it.next();
            if (a.getBonds().size() != n_bonds[i]) {
                return getError(2);
            }
            if (a.getType() != types[i]) {
                return getError(3);
            }
            i++;
        }
        return null;
    }
}
