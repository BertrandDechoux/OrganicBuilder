package uk.org.squirm3.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ReactionTest {

    /**
     * Test of the 'tostring' method. *
     */
    @Test
    public void testToString() {
        final int a_type = 1;
        final int a_state = 2;
        final boolean bonded_before = false;
        final int b_type = 3;
        final int b_state = 4;
        final int future_a_state = 5;
        final boolean bonded_after = true;
        final int future_b_state = 6;
        final Reaction r = new Reaction(a_type, a_state, bonded_before, b_type,
                b_state, future_a_state, bonded_after, future_b_state);
        assertTrue(r.toString().equals("b2 + d4 => b5d6"));
    }

    /**
     * Test of the reaction process. *
     */
    @Test
    public void testTryReaction() {
        // creation of the reaction
        final int a_type = 0;
        final int a_state = 0;
        final boolean bonded_before = false;
        final int b_type = 1;
        final int b_state = 1;
        final int future_a_state = 2;
        final boolean bonded_after = true;
        final int future_b_state = 2;
        final Reaction r = new Reaction(a_type, a_state, bonded_before, b_type,
                b_state, future_a_state, bonded_after, future_b_state);
        // creation of the atoms
        final Atom a1 = new Atom(new MobilePoint(), 0, 0);
        final Atom a2 = new Atom(new MobilePoint(), 1, 1);
        // test of the reaction
        r.tryOn(a1, a2);
        assertTrue(a1.getType() == 0 && a1.getState() == 2 && a2.getType() == 1
                && a2.getState() == 2 && a1.hasBondWith(a2));
        // test when no reaction should occur
        a1.setState(0);
        r.tryOn(a1, a2);
        assertTrue(a1.getType() == 0 && a1.getState() == 0 && a2.getType() == 1
                && a2.getState() == 2 && a1.hasBondWith(a2));
    }

}
