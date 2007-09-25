package test.uk.org.squirm3.data;

import java.util.LinkedList;

import junit.framework.TestCase;
import uk.org.squirm3.data.Atom;

/**  
Copyright 2007 Bertrand Dechoux

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

public class AtomTest extends TestCase {

	/** Test of the constructor. **/
	public void testAtom() {
		float x = 1;
		float y = 2;
		float ms = 3;
		int t = 4;
		int s = 5;
		Atom a = new Atom(x, y, t, s, ms);
		assertTrue(a.pos.x == x);
		assertTrue(a.pos.y == y);
		assertTrue(a.type == t);
		assertTrue(a.state == s);
	}
	
	/** Test de la propriete stuck. **/
	public void testStuck() {
		Atom a = new Atom(0, 0, 0, 0, 0);
		assertFalse(a.isStuck());
		
		a.setStuck(false);
		assertFalse(a.isStuck());
		
		a.setStuck(true);
		assertTrue(a.isStuck());
		
		a.setStuck(false);
		assertFalse(a.isStuck());
	}
	
	/** Test de la propriete killer. **/
	public void testKiller() {
		Atom a = new Atom(0, 0, 0, 0, 0);
		assertFalse(a.isKiller());
		
		a.setKiller(false);
		assertFalse(a.isKiller());
		
		a.setKiller(true);
		assertTrue(a.isKiller());
		
		a.setKiller(false);
		assertFalse(a.isKiller());		
	}
	
	/** Test de la propriete reacted. **/
	public void testReacted() {
		Atom a = new Atom(0, 0, 0, 0, 0);
		assertFalse(a.hasReacted());
		
		a.setReacted(false);
		assertFalse(a.hasReacted());
		
		a.setReacted(true);
		assertTrue(a.hasReacted());
		
		a.setReacted(false);
		assertFalse(a.hasReacted());		
	}

	/** Test of basic bonding and breaking. */
	public void testBondingAndBreaking() {
		Atom a1 = new Atom(0, 0, 0, 1, 0);
		Atom a2 = new Atom(0, 0, 0, 2, 0);
		assertFalse(a1.hasBondWith(a2));
		assertFalse(a2.hasBondWith(a1));
		
		a1.bondWith(a2);
		assertTrue(a1.hasBondWith(a2));
		assertTrue(a2.hasBondWith(a1));

		
		a1.breakBondWith(a2);
		assertFalse(a1.hasBondWith(a2));
		assertFalse(a2.hasBondWith(a1));
	}

	/** Test of breakAllBonds() and getAllConnectedAtoms(). **/
	public void testGeneralMethods() {
		Atom a1 = new Atom(0, 0, 0, 1, 0);
		Atom a2 = new Atom(0, 0, 0, 2, 0);
		Atom a3 = new Atom(0, 0, 0, 3, 0);
		Atom a4 = new Atom(0, 0, 0, 4, 0);
		Atom a5 = new Atom(0, 0, 0, 3, 0);
		Atom a6 = new Atom(0, 0, 0, 4, 0);
		
		a1.bondWith(a2);
		a1.bondWith(a3);
		a1.bondWith(a6);
		a3.bondWith(a4);
		a3.bondWith(a5);
		a5.bondWith(a6);
		
		LinkedList l = new LinkedList();
		a1.getAllConnectedAtoms(l);
		
		assertTrue(l.contains(a1));
		assertTrue(l.contains(a2));
		assertTrue(l.contains(a3));
		assertTrue(l.contains(a4));
		assertTrue(l.contains(a5));
		assertTrue(l.contains(a6));
		
		a1.breakAllBonds();
		l.clear();
		a1.getAllConnectedAtoms(l);
		assertTrue(l.contains(a1));
		l.remove(a1);
		assertTrue(l.isEmpty());
	}

	/** Test of the string representation. **/
	public void testToString() {
		Atom a = new Atom(0, 0, 1, 1, 0);
		assertTrue("b1".equals(a.toString()));
	}
}
