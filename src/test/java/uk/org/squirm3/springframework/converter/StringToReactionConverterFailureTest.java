package uk.org.squirm3.springframework.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.org.squirm3.model.Reaction;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Parameterized.class)
public class StringToReactionConverterFailureTest {

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Collection<Object[]> parameters = new ArrayList<Object[]>();
        parameters.add(new Object[]{"a0 + a0 => a0 + b0"});
        parameters.add(new Object[]{"a0 + k0 => a0 + k0"});
        parameters.add(new Object[]{"a0 + K0 => a0 + K0"});
        parameters.add(new Object[]{"a0 + a0 > a0 + a0"});
        parameters.add(new Object[]{"a0 + a0 = a0 + a0"});
        parameters.add(new Object[]{"a + a0 => a0 + a0"});
        parameters.add(new Object[]{"a0 + a => a0 + a0"});
        parameters.add(new Object[]{"0 + a0 => a0 + a0"});
        parameters.add(new Object[]{"a0 + 0 => a0 + a0"});
        parameters.add(new Object[]{"a0 + a0 => a + a0"});
        parameters.add(new Object[]{"a0 + a0 => 0 + a0"});
        parameters.add(new Object[]{"a0 + a0 => a0 + a"});
        parameters.add(new Object[]{"a0 + a0 => a0 + 0"});
        return parameters;
    }

    private final StringToReactionConverter converter = new StringToReactionConverter(
            new CharacterToReactionTypeConverter());

    private final String reactionString;

    public StringToReactionConverterFailureTest(final String reactionString) {
        this.reactionString = reactionString;
    }

    @Test
    public void shouldParseReaction() {
        try {
            final Reaction reaction = converter.convert(reactionString);
            assertThat(reaction).isNull();
        } catch (final Exception e) {
            return;
        }
        Assert.fail("Parsing should heve failed before");
    }

}
