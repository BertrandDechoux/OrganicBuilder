package test.uk.org.squirm3;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import test.uk.org.squirm3.data.AtomTest;
import test.uk.org.squirm3.data.DraggingPointTest;
import test.uk.org.squirm3.data.MobilePointTest;
import test.uk.org.squirm3.data.ReactionTest;

public class AllJUnitTests extends TestCase {

	public AllJUnitTests(String name) {
		super(name);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(AtomTest.class);
		suite.addTest(new TestSuite(DraggingPointTest.class));
		suite.addTest(new TestSuite(MobilePointTest.class));
		suite.addTest(new TestSuite(ReactionTest.class));
		return suite;
	}
}
