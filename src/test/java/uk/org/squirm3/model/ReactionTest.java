package uk.org.squirm3.model;

import org.junit.Test;

import uk.org.squirm3.model.type.def.BasicType;
import static org.fest.assertions.Assertions.assertThat;

public class ReactionTest {

    /**
     * Test of the 'tostring' method. *
     */
    @Test
    public void testToString() {
        final Reaction reaction = new Reaction(BasicType.B, 2, false,
                BasicType.D, 4, 5, true, 6);
        assertThat(reaction.toString()).isEqualTo("b2 + d4 => b5d6");
    }

}
