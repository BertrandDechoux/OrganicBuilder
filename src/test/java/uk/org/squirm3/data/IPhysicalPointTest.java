package uk.org.squirm3.data;

import uk.org.squirm3.data.IPhysicalPoint;
import junit.framework.TestCase;

/**  
${my.copyright}
 */

public abstract class IPhysicalPointTest extends TestCase {
    protected IPhysicalPoint iPhysicalPoint;

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.getPositionX()' */
    public void testGetPositionX() {
        assertTrue(iPhysicalPoint.getPositionX()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.getPositionY()' */
    public void testGetPositionY() {
        assertTrue(iPhysicalPoint.getPositionY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.getSpeedX()' */
    public void testGetSpeedX() {
        assertTrue(iPhysicalPoint.getSpeedX()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.getSpeedY()' */
    public void testGetSpeedY() {
        assertTrue(iPhysicalPoint.getSpeedY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.getAccelerationX()' */
    public void testGetAccelerationX() {
        assertTrue(iPhysicalPoint.getAccelerationX()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.getAccelerationY()' */
    public void testGetAccelerationY() {
        assertTrue(iPhysicalPoint.getAccelerationY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.setPositionX(float)' */
    public void testSetPositionX() {
        final boolean result = iPhysicalPoint.setPositionX(1);
        assertTrue((!result && iPhysicalPoint.getPositionX()==0) ||
                (result && iPhysicalPoint.getPositionX()==1));
        assertTrue(iPhysicalPoint.getPositionY()==0);
        assertTrue(iPhysicalPoint.getSpeedX()==0);
        assertTrue(iPhysicalPoint.getSpeedY()==0);
        assertTrue(iPhysicalPoint.getAccelerationX()==0);
        assertTrue(iPhysicalPoint.getAccelerationY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.setPositionY(float)' */
    public void testSetPositionY() {
        final boolean result = iPhysicalPoint.setPositionY(1);
        assertTrue((!result && iPhysicalPoint.getPositionY()==0) ||
                (result && iPhysicalPoint.getPositionY()==1));
        assertTrue(iPhysicalPoint.getPositionX()==0);
        assertTrue(iPhysicalPoint.getSpeedX()==0);
        assertTrue(iPhysicalPoint.getSpeedY()==0);
        assertTrue(iPhysicalPoint.getAccelerationX()==0);
        assertTrue(iPhysicalPoint.getAccelerationY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.setSpeedX(float)' */
    public void testSetSpeedX() {
        final boolean result = iPhysicalPoint.setSpeedX(1);
        assertTrue((!result && iPhysicalPoint.getSpeedX()==0) ||
                (result && iPhysicalPoint.getSpeedX()==1));
        assertTrue(iPhysicalPoint.getPositionX()==0);
        assertTrue(iPhysicalPoint.getPositionY()==0);
        assertTrue(iPhysicalPoint.getSpeedY()==0);
        assertTrue(iPhysicalPoint.getAccelerationX()==0);
        assertTrue(iPhysicalPoint.getAccelerationY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.setSpeedY(float)' */
    public void testSetSpeedY() {
        final boolean result = iPhysicalPoint.setSpeedY(1);
        assertTrue((!result && iPhysicalPoint.getSpeedY()==0) ||
                (result && iPhysicalPoint.getSpeedY()==1));
        assertTrue(iPhysicalPoint.getPositionX()==0);
        assertTrue(iPhysicalPoint.getPositionY()==0);
        assertTrue(iPhysicalPoint.getSpeedX()==0);
        assertTrue(iPhysicalPoint.getAccelerationX()==0);
        assertTrue(iPhysicalPoint.getAccelerationY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.setAccelerationX(float)' */
    public void testSetAccelerationX() {
        final boolean result = iPhysicalPoint.setAccelerationX(1);
        assertTrue((!result && iPhysicalPoint.getAccelerationX()==0) ||
                (result && iPhysicalPoint.getAccelerationX()==1));
        assertTrue(iPhysicalPoint.getPositionX()==0);
        assertTrue(iPhysicalPoint.getPositionY()==0);
        assertTrue(iPhysicalPoint.getSpeedX()==0);
        assertTrue(iPhysicalPoint.getSpeedY()==0);
        assertTrue(iPhysicalPoint.getAccelerationY()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.setAccelerationY(float)' */
    public void testSetAccelerationY() {
        final boolean result = iPhysicalPoint.setAccelerationY(1);
        assertTrue((!result && iPhysicalPoint.getAccelerationY()==0) ||
                (result && iPhysicalPoint.getAccelerationY()==1));
        assertTrue(iPhysicalPoint.getPositionX()==0);
        assertTrue(iPhysicalPoint.getPositionY()==0);
        assertTrue(iPhysicalPoint.getSpeedX()==0);
        assertTrue(iPhysicalPoint.getSpeedY()==0);
        assertTrue(iPhysicalPoint.getAccelerationX()==0);
    }

    /* Test method for 'uk.org.squirm3.data.IPhysicalPoint.copy()' */
    public void testCopy() {
        IPhysicalPoint copy = iPhysicalPoint.copy();
        assertTrue(copy!=iPhysicalPoint || (
                !iPhysicalPoint.setPositionX(1) && !iPhysicalPoint.setPositionY(1) &&
                !iPhysicalPoint.setSpeedX(1) && !iPhysicalPoint.setSpeedY(1) &&
                !iPhysicalPoint.setAccelerationX(1) && !iPhysicalPoint.setAccelerationY(1)));
    }

}
