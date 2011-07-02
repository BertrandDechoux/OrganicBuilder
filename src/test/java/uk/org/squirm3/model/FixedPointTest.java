package uk.org.squirm3.model;

public class FixedPointTest extends IPhysicalPointTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        iPhysicalPoint = new FixedPoint(0, 0);
    }
}
