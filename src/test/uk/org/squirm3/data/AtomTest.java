package test.uk.org.squirm3.data;

import java.util.LinkedList;

import junit.framework.TestCase;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.FixedPoint;
import uk.org.squirm3.data.MobilePoint;

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
		int t = 4;
		int s = 5;
		Atom a = new Atom(new MobilePoint(), t, s);
		assertTrue(a.getType() == t);
		assertTrue(a.getState() == s);
	}
	
	/** Test de la propriete stuck. **/
	public void testStuck() {
		Atom a = new Atom(new MobilePoint(), 0, 0);
		assertFalse(a.isStuck());
		
		a = new Atom(new FixedPoint(0,0), 0, 0);
		assertTrue(a.isStuck());
	}
	
	/** Test de la propriete killer. **/
	public void testKiller() {
		Atom a = new Atom(new MobilePoint(), 0, 0);
		assertFalse(a.isKiller());
		
		a = new Atom(new MobilePoint(), Atom.KILLER_TYPE, 0);
		assertTrue(a.isKiller());		
	}

	/** Test of basic bonding and breaking. */
	public void testBondingAndBreaking() {
		Atom a1 = new Atom(new MobilePoint(), 0, 1);
		Atom a2 = new Atom(new MobilePoint(), 0, 2);
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
		MobilePoint physicalPoint = new MobilePoint();
		Atom a1 = new Atom(physicalPoint, 0, 1);
		Atom a2 = new Atom(physicalPoint, 0, 2);
		Atom a3 = new Atom(physicalPoint, 0, 3);
		Atom a4 = new Atom(physicalPoint, 0, 4);
		Atom a5 = new Atom(physicalPoint, 0, 3);
		Atom a6 = new Atom(physicalPoint, 0, 4);
		
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
		Atom a = new Atom(new MobilePoint(), 1, 1);
		assertTrue("b1".equals(a.toString()));
	}
}
