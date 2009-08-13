package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * ${my.copyright}
 */
public class Grow_membrane extends Level {

    public Grow_membrane(String title, String challenge, String hint, String[] errors,
                         Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        final IPhysicalPoint mobilePoint = new MobilePoint();
        // place and bond 8 atoms to form a loop
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
            for (int i = 0; i < atoms.length; i++)
                if (atoms[i].getType() == 5) return atoms;
        }
        return null;
    }

    public String evaluate(Atom[] atoms) {
        // starting from atom 0 there should be a neat closed loop of a atoms
        LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        // (each atom in the connected group should of type 'a' and have exactly two bonds (hence a neat loop))
        int x_points[] = new int[joined.size()], y_points[] = new int[joined.size()];
        Iterator it = joined.iterator();
        int i = 0;
        while (it.hasNext()) {
            Atom a = (Atom) it.next();
            if (a.getType() != 0 || a.getBonds().size() != 2)
                return getError(1);
            x_points[i] = (int) a.getPhysicalPoint().getPositionX(); // (need these for polygon check, below)
            y_points[i] = (int) a.getPhysicalPoint().getPositionY();
            i++;
        }
        // inside the polygon formed by the a atoms there should be exactly one atom - the original f1 (although its state may have changed)
        Atom f1 = atoms[8]; // see the setup code for this level
        Polygon poly = new Polygon(x_points, y_points, joined.size());
        if (!poly.contains(new Point2D.Float(f1.getPhysicalPoint().getPositionX(), f1.getPhysicalPoint().getPositionY())))
            return getError(2);
        // and no other 'a' atoms around
        for (i = 0; i < atoms.length; i++) {
            Atom a = atoms[i];
            if (!joined.contains(a) && a.getType() == 0)
                return getError(3);
        }
        return null;
    }
}
