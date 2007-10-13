package test.uk.org.squirm3.data;

import java.util.Vector;

import junit.framework.TestCase;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.MobilePoint;
import uk.org.squirm3.data.Reaction;


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

public class ReactionTest extends TestCase {

	/** Test of the 'tostring' method. **/
	public void testToString() {
		int a_type = 1;
		int a_state = 2;
		boolean bonded_before = false;
		int b_type = 3;
		int b_state = 4;
		int future_a_state = 5;
		boolean bonded_after = true;
		int future_b_state = 6;
		Reaction r = new Reaction(a_type, a_state, bonded_before,
				b_type, b_state, future_a_state,
				bonded_after, future_b_state);
		assertTrue(r.toString().equals("b2 + d4 => b5d6"));
	}

	/** Test of the 'parse' method. **/
	public void testParse() {
		int a_type = 1;
		int a_state = 2;
		boolean bonded_before = false;
		int b_type = 3;
		int b_state = 4;
		int future_a_state = 5;
		boolean bonded_after = true;
		int future_b_state = 6;
		Reaction r = new Reaction(a_type, a_state, bonded_before,
				b_type, b_state, future_a_state,
				bonded_after, future_b_state);
		Vector v = new Vector(0);
		Reaction.parse(r.toString(),v);
		assertTrue(r.toString().equals(v.firstElement().toString()));
	}

	/** Test of the reaction process. **/
	public void testTryReaction() {
		// creation of the reaction
		int a_type = 0;
		int a_state = 0;
		boolean bonded_before = false;
		int b_type = 1;
		int b_state = 1;
		int future_a_state = 2;
		boolean bonded_after = true;
		int future_b_state = 2;
		Reaction r = new Reaction(a_type, a_state, bonded_before,
				b_type, b_state, future_a_state,
				bonded_after, future_b_state);
		Vector v = new Vector();
		v.add(r);
		// creation of the atoms
		Atom a1 = new Atom(new MobilePoint(), 0, 0);
		Atom a2 = new Atom(new MobilePoint(), 1, 1);
		// test of the reaction
		Reaction.tryReaction(a1, a2, v);
		assertTrue(a1.getType()==0 && a1.getState()==2
				&& a2.getType()==1 && a2.getState()==2
				&& a1.hasBondWith(a2));
		// test when no reaction should occur
		a1.setState(0);
		Reaction.tryReaction(a1, a2, v);
		assertTrue(a1.getType()==0 && a1.getState()==0
				&& a2.getType()==1 && a2.getState()==2
				&& a1.hasBondWith(a2));
	}

}
