package uk.org.squirm3.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DraggingPointTest {

    /**
     * Test of the constructor. *
     */
    @Test
    public void testDraggingPoint() {
        double x = 1;
        double y = 2;
        int index = 3;
        DraggingPoint p = new DraggingPoint(x, y, index);
        assertTrue(p.getX() == x);
        assertTrue(p.getY() == y);
        assertTrue(p.getWhichBeingDragging() == index);
    }

    /**
     * Test of the 'equals' method. *
     */
    @Test
    public void testEqualsObject() {
        DraggingPoint p1 = new DraggingPoint(0.1, 0.2, 3);
        DraggingPoint p2 = new DraggingPoint(0.1, 0.2, 3);
        DraggingPoint p3 = new DraggingPoint(0.2, 0.2, 3);

        assertTrue(p1.equals(p1));
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
    }

}
