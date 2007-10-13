package test.uk.org.squirm3.data;

import uk.org.squirm3.data.MobilePoint;

public class MobilePointTest extends IPhysicalPointTest {
	protected void setUp() throws Exception {
		super.setUp();
		iPhysicalPoint = new MobilePoint();
	}
}
