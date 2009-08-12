package uk.org.squirm3.data;

import uk.org.squirm3.data.FixedPoint;

/**  
${my.copyright}
 */

public class FixedPointTest extends IPhysicalPointTest {
    protected void setUp() throws Exception {
        super.setUp();
        iPhysicalPoint = new FixedPoint((float)0, (float)0);
    }
}
