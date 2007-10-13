package uk.org.squirm3.data;

import java.util.Iterator;
import java.util.LinkedList;

/**  
Copyright 2007 Tim J. Hutton, Ralph Hartley, Bertrand Dechoux

This file is part of Organic Builder.

Organic Builder is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Organic Builder is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Organic Builder; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class Atom 
{
	// TODO should not be hardcoded, properties file ?
	static private final float R = 22.0f;
	// acceleration only used in new correct physics code
	private IPhysicalPoint iPhysicalPoint;
	private int state; // type: 0=a,..5=f
	private int type;
	private LinkedList bonds;
	private boolean stuck=false; // special marker for atoms that don't move
	private boolean killer=false; // special marker for atoms that have a special caustic effect
	private boolean has_reacted=false; // has this atom been part of a reaction this timestep?
	static final public String type_code = "abcdefxy";


	public Atom(IPhysicalPoint iPhysicalPoint,int t,int s) {
		this.iPhysicalPoint = iPhysicalPoint.copy();
		setType(t);
		setState(s);
		bonds = new LinkedList();
	}
	
	//TODO the copy should not allow modifications
	public LinkedList getBonds() {
		return bonds;
	}
	
	//TODO remove, use a final field instead and create
	// a new object if needed
	public void setStuck(boolean b) {
		stuck = b;
	}

	public boolean isStuck() {
		return stuck;
	}

	//TODO remove, use a final field instead and create
	// a new object if needed
	public void setKiller(boolean b) {
		killer = b;
	}
	
	public boolean isKiller() {
		return killer;
	}
	
	public void setReacted(boolean b) {
		has_reacted = b;
	}
	
	public boolean hasReacted() {
		return has_reacted;
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
		return type_code.charAt(getType()) + String.valueOf(getState());
	}
	
	public static float getAtomSize(){
		return R;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public IPhysicalPoint getPhysicalPoint() {
		return iPhysicalPoint;
	}
	
} // class sq3Atom
