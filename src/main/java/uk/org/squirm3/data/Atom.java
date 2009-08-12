package uk.org.squirm3.data;

import java.util.Iterator;
import java.util.LinkedList;

/**  
${my.copyright}
 */

public class Atom {
    // TODO should not be hardcoded, properties file ?
    static private final float R = 22.0f;

    private IPhysicalPoint iPhysicalPoint;
    private int state; // type: 0=a,..5=f
    private final int type;
    private final LinkedList bonds;
    static final public String type_code = "abcdefxy";

    public static final int KILLER_TYPE = -1;  // special marker for atoms that have a special caustic effect
    private static final char killer_char = 'K';


    public Atom(IPhysicalPoint iPhysicalPoint,int t,int s) {
        this.iPhysicalPoint = iPhysicalPoint.copy();
        type = t;
        setState(s);
        bonds = new LinkedList();
    }

    public void bondWith(Atom other) {
        if(!hasBondWith(other)) {
            this.bonds.add(other);
            other.bonds.add(this);
        }
    }

    public boolean hasBondWith(Atom other) {
        return bonds.contains(other);
    }

    public void getAllConnectedAtoms(LinkedList list) {
        // is this a new atom for this list?
        if(list.contains(this)) return;
        // if no, add this one, and all connected atoms
        list.add(this);
        // recurse
        Iterator it = bonds.iterator();
        while(it.hasNext()) {
            ((Atom)it.next()).getAllConnectedAtoms(list);
        }
    }

    public void breakBondWith(Atom other) {
        if(hasBondWith(other)) {
            this.bonds.remove(other);
            other.bonds.remove(this);
        }
    }

    public void breakAllBonds() {
        // slower method but avoid the concurrent exception
        // TODO faster one, using synchronisation ?
        Object a[] = bonds.toArray();
        for(int i = 0; i < a.length ; i++)
            breakBondWith((Atom)a[i]);
        /*
		Iterator it = bonds.iterator();
		while(it.hasNext()) {
			breakBondWith((Atom)it.next());
		} */
    }

    public String toString() {
        if(type==KILLER_TYPE) return killer_char + String.valueOf(getState());
        return type_code.charAt(getType()) + String.valueOf(getState());
    }

    //TODO find a better way
    public boolean isStuck() {
        return iPhysicalPoint instanceof FixedPoint;
    }

    //TODO the copy should not allow modifications
    public LinkedList getBonds() { return bonds; }

    public boolean isKiller() { return type==KILLER_TYPE; }

    public void setState(int state) { this.state = state; }

    public int getState() { return state; }

    public int getType() { return type; }

    public IPhysicalPoint getPhysicalPoint() { return iPhysicalPoint; }

    public static float getAtomSize(){ return R; }

} // class sq3Atom
