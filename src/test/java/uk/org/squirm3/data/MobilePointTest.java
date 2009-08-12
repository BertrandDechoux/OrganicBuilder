package uk.org.squirm3.data;

import uk.org.squirm3.data.MobilePoint;

/**  
${my.copyright}
 */

public class MobilePointTest extends IPhysicalPointTest {
    protected void setUp() throws Exception {
        super.setUp();
        iPhysicalPoint = new MobilePoint();
    }
}
