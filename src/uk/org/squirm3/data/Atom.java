package uk.org.squirm3.data;
import java.awt.geom.Point2D;
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
along with Foobar; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class Atom 
{
	static private final float R = 22.0f;
	//TODO should not be hardcoded, properties file ?
	
	//TODO remove "public" !!!
	public Point2D.Float pos,velocity,acceleration; // acceleration only used in new correct physics code
	public int type,state; // type: 0=a,..5=f

	public LinkedList bonds;
	
	public boolean stuck=false; // special marker for atoms that don't move
	public boolean killer=false; // special marker for atoms that have a special caustic effect
	
	public boolean has_reacted=false; // has this atom been part of a reaction this timestep?
	
	static final public String type_code = "abcdefxy";
	
	public Atom(float x,float y,int t,int s,float ms) {
		this.pos = new Point2D.Float(x,y);
		this.velocity = new Point2D.Float((float)(Math.random()*ms-ms/2.0),(float)(Math.random()*ms-ms/2.0));
		this.acceleration = new Point2D.Float(0,0);
		this.type = t;
		this.state = s;
		this.bonds = new LinkedList();
	}
	
	public void bondWith(Atom other) {
		// could check for existing bond if you were worried
		//if(this.bonds.contains(other) || other.bonds.contains(this)) {}

		this.bonds.add(other);
		other.bonds.add(this);
	}
	
	public boolean hasBondWith(Atom other) {
		Iterator it = bonds.iterator();
		while(it.hasNext()) {
			if(it.next()==other) return true;
		}
		return false;
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
		Iterator it = bonds.iterator();
		while(it.hasNext()) {
			Object atom = it.next();
			if(atom==other) bonds.remove(atom);
		}
		it = other.bonds.iterator();
		while(it.hasNext()) {
			Object atom = it.next();
			if(atom==other) other.bonds.remove(atom);
		}
	}

	public void breakAllBonds() {
		Iterator it = bonds.iterator();
		while(it.hasNext()) {
			breakBondWith((Atom)it.next());
		}
	}
	
	public String toString() {
		return type_code.charAt(type) + String.valueOf(state);
	}
	
	public static float getAtomSize(){
		return R;
	}
	
} // class sq3Atom
