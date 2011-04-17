package uk.org.squirm3.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DraggingPointTest {

    /**
     * Test of the constructor. *
     */
    @Test
    public void testDraggingPoint() {
        final long x = 1;
        final long y = 2;
        final int index = 3;
        final DraggingPoint p = new DraggingPoint(x, y, index);
        assertTrue(p.getX() == x);
        assertTrue(p.getY() == y);
        assertTrue(p.getWhichBeingDragging() == index);
    }

    /**
     * Test of the 'equals' method. *
     */
    @Test
    public void testEqualsObject() {
        final DraggingPoint p1 = new DraggingPoint(1, 2, 3);
        final DraggingPoint p2 = new DraggingPoint(1, 2, 3);
        final DraggingPoint p3 = new DraggingPoint(2, 2, 3);

        assertTrue(p1.equals(p1));
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
    }

}
