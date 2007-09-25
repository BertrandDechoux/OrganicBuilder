package test.uk.org.squirm3.data;

import junit.framework.TestCase;
import uk.org.squirm3.data.DraggingPoint;


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

public class DraggingPointTest extends TestCase {

	/** Test of the constructor. **/
	public void testDraggingPoint() {
		long x = 1;
		long y = 2;
		int index = 3;
		DraggingPoint p = new DraggingPoint(x, y, index);
		assertTrue(p.getX()==x);
		assertTrue(p.getY()==y);
		assertTrue(p.getWhichBeingDragging()==index);
	}

	/** Test of the 'equals' method. **/
	public void testEqualsObject() {
		DraggingPoint p1 = new DraggingPoint(1, 2, 3);
		DraggingPoint p2 = new DraggingPoint(1, 2, 3);
		DraggingPoint p3 = new DraggingPoint(2, 2, 3);
		
		assertTrue(p1.equals(p1));
		assertTrue(p1.equals(p2));
		assertFalse(p1.equals(p3));
	}

}
