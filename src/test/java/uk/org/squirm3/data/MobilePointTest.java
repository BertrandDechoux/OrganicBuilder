package uk.org.squirm3.data;

public class MobilePointTest extends IPhysicalPointTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        iPhysicalPoint = new MobilePoint();
    }
}
