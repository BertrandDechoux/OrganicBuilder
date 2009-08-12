package uk.org.squirm3.data;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * ${my.copyright}
 */

public class ReactionTest {

    /**
     * Test of the 'tostring' method. *
     */
    @Test
    public void testToString() {
        int a_type = 1;
        int a_state = 2;
        boolean bonded_before = false;
        int b_type = 3;
        int b_state = 4;
        int future_a_state = 5;
        boolean bonded_after = true;
        int future_b_state = 6;
        Reaction r = new Reaction(a_type, a_state, bonded_before,
                b_type, b_state, future_a_state,
                bonded_after, future_b_state);
        assertTrue(r.toString().equals("b2 + d4 => b5d6"));
    }

    /**
     * Test of the 'parse' method. *
     */
    @Test
    public void testParse() {
        Reaction r = new Reaction(1, 2, false, 3, 4, 5, true, 6);
        Reaction parsedReaction = Reaction.parse(r.toString());
        assertTrue(r.toString().equals(parsedReaction.toString()));
        r = new Reaction(6, 20345, true, 7, 4234, 5, false, 6);
        parsedReaction = Reaction.parse("x20345y4234-> \tx5   +  y6");
        assertTrue(r.toString().equals(parsedReaction.toString()));
    }

    /**
     * Test of the reaction process. *
     */
    @Test
    public void testTryReaction() {
        // creation of the reaction
        int a_type = 0;
        int a_state = 0;
        boolean bonded_before = false;
        int b_type = 1;
        int b_state = 1;
        int future_a_state = 2;
        boolean bonded_after = true;
        int future_b_state = 2;
        Reaction r = new Reaction(a_type, a_state, bonded_before,
                b_type, b_state, future_a_state,
                bonded_after, future_b_state);
        // creation of the atoms
        Atom a1 = new Atom(new MobilePoint(), 0, 0);
        Atom a2 = new Atom(new MobilePoint(), 1, 1);
        // test of the reaction
        r.tryOn(a1, a2);
        assertTrue(a1.getType() == 0 && a1.getState() == 2
                && a2.getType() == 1 && a2.getState() == 2
                && a1.hasBondWith(a2));
        // test when no reaction should occur
        a1.setState(0);
        r.tryOn(a1, a2);
        assertTrue(a1.getType() == 0 && a1.getState() == 0
                && a2.getType() == 1 && a2.getState() == 2
                && a1.hasBondWith(a2));
    }

}
