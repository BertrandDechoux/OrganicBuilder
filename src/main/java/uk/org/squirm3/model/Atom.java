package uk.org.squirm3.model;

import java.util.Iterator;
import java.util.LinkedList;

import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.def.SpecialType;

public class Atom {
    // TODO should not be hardcoded, properties file ?
    static private final float R = 22.0f;

    private final IPhysicalPoint iPhysicalPoint;
    private int state;
    private final AtomType type;
    private final LinkedList<Atom> bonds;

    public Atom(final IPhysicalPoint iPhysicalPoint, final AtomType type,
            final int state) {
        this.iPhysicalPoint = iPhysicalPoint.copy();
        this.type = type;
        this.state = state;
        bonds = new LinkedList<Atom>();
    }

    public void bondWith(final Atom other) {
        if (!hasBondWith(other)) {
            bonds.add(other);
            other.bonds.add(this);
        }
    }

    public boolean hasBondWith(final Atom other) {
        return bonds.contains(other);
    }

    public void getAllConnectedAtoms(final LinkedList<Atom> list) {
        // is this a new atom for this list?
        if (list.contains(this)) {
            return;
        }
        // if no, add this one, and all connected atoms
        list.add(this);
        // recurse
        final Iterator<Atom> it = bonds.iterator();
        while (it.hasNext()) {
            it.next().getAllConnectedAtoms(list);
        }
    }

    public void breakBondWith(final Atom other) {
        if (hasBondWith(other)) {
            bonds.remove(other);
            other.bonds.remove(this);
        }
    }

    public void breakAllBonds() {
        // slower method but avoid the concurrent exception
        // TODO faster one, using synchronisation ?
        final Object a[] = bonds.toArray();
        for (final Object element : a) {
            breakBondWith((Atom) element);
            /*
             * Iterator it = bonds.iterator(); while(it.hasNext()) {
             * breakBondWith((Atom)it.next()); }
             */
        }
    }

    @Override
    public String toString() {
        return "" + type.getCharacterIdentifier() + state;
    }

    // TODO find a better way
    public boolean isStuck() {
        return iPhysicalPoint instanceof FixedPoint;
    }

    // TODO the copy should not allow modifications
    public LinkedList<Atom> getBonds() {
        return bonds;
    }

    public boolean isKiller() {
        return type == SpecialType.KILLER;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public AtomType getType() {
        return type;
    }

    public IPhysicalPoint getPhysicalPoint() {
        return iPhysicalPoint;
    }

    public static float getAtomSize() {
        return R;
    }

}
