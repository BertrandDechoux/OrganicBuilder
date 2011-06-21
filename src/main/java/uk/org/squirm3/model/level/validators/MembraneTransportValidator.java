package uk.org.squirm3.model.level.validators;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;

public class MembraneTransportValidator implements AtomValidator {

    private Atom prisoner;
    private Atom membraneSeed;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        prisoner = AtomSelector.findUnique("f1", atoms);
        membraneSeed = AtomSelector.findUnique("a4", atoms);
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        // starting from atom 0 there should be a neat closed loop of a atoms
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        membraneSeed.getAllConnectedAtoms(joined);
        // (each atom in the connected group should of type 'a' and have exactly
        // two bonds (hence a neat loop))
        final int x_points[] = new int[joined.size()], y_points[] = new int[joined
                .size()];
        final Iterator<Atom> it = joined.iterator();
        int i = 0;
        while (it.hasNext()) {
            final Atom a = it.next();
            if (a.getType() != 0 || a.getBonds().size() != 2) {
                return messages.getError(1);
            }
            x_points[i] = (int) a.getPhysicalPoint().getPositionX(); // (need
                                                                     // these
                                                                     // for
                                                                     // polygon
                                                                     // check,
                                                                     // below)
            y_points[i] = (int) a.getPhysicalPoint().getPositionY();
            i++;
        }
        // inside should be the original 'b' atom, and all the 'f' atoms, and
        // nothing else
        final Atom b1 = prisoner; // see the setup code for this level
        final Polygon poly = new Polygon(x_points, y_points, joined.size());
        if (!poly.contains(new Point2D.Float(b1.getPhysicalPoint()
                .getPositionX(), b1.getPhysicalPoint().getPositionY()))) {
            return messages.getError(2);
        }
        // check the other atoms (want: f's inside, other's outside)
        for (final Atom a : atoms) {
            if (a.getType() == 5
                    && !poly.contains(new Point2D.Float(a.getPhysicalPoint()
                            .getPositionX(), a.getPhysicalPoint()
                            .getPositionY()))) {
                return messages.getError(3);
            } else if (a.getType() != 5
                    && poly.contains(new Point2D.Float(a.getPhysicalPoint()
                            .getPositionX(), a.getPhysicalPoint()
                            .getPositionY()))) {
                return messages.getError(4);
            }
        }
        return null;
    }

}
