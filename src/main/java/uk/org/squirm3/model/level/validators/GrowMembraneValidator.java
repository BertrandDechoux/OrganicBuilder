package uk.org.squirm3.model.level.validators;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.AtomSelector;
import uk.org.squirm3.model.level.AtomValidator;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.def.BasicType;

public class GrowMembraneValidator implements AtomValidator {

    private Atom prisoner;
    private Atom membraneSeed;

    @Override
    public void setup(final Collection<? extends Atom> atoms) {
        prisoner = AtomSelector.findUnique(BasicType.F, 1, atoms);
        membraneSeed = AtomSelector.findUnique(BasicType.A, 4, atoms);
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms,
            final LevelMessages messages) {
        // starting from atom 0 there should be a neat closed loop of a atoms
        final LinkedList<Atom> joined = new LinkedList<Atom>();
        membraneSeed.getAllConnectedAtoms(joined);
        // (each atom in the connected group should of type 'a' and have exactly
        // two bonds (hence a neat loop))
        final double points[] = new double[joined.size() * 2];
        final Iterator<Atom> it = joined.iterator();
        int i = 0;
        while (it.hasNext()) {
            final Atom a = it.next();
            if (a.getType() != BasicType.A || a.getBonds().size() != 2) {
                return messages.getError(1);
            }
            points[i] = a.getPhysicalPoint().getPositionX(); // (need
                                                                     // these
                                                                     // for
                                                                     // polygon
                                                                     // check,
                                                                     // below)
            points[i + 1] = a.getPhysicalPoint().getPositionY();
            i++;
        }
        // inside the polygon formed by the a atoms there should be exactly one
        // atom - the original f1 (although its state may have changed)
        final Atom f1 = prisoner; // see the setup code for this level
        final Polygon poly = new Polygon(points);
        if (!poly.contains(new Point2D(f1.getPhysicalPoint()
                .getPositionX(), f1.getPhysicalPoint().getPositionY()))) {
            return messages.getError(2);
        }
        // and no other 'a' atoms around
        for (final Atom a : atoms) {
            if (!joined.contains(a) && a.getType() == BasicType.A) {
                return messages.getError(3);
            }
        }
        return null;
    }

}
