package uk.org.squirm3.springframework.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.org.squirm3.model.Reaction;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(Parameterized.class)
public class StringToReactionConverterSuccessTest {

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Collection<Object[]> parameters = new ArrayList<Object[]>();
        parameters.add(new Object[]{"a0 + a0 => a0 + a0"});
        parameters.add(new Object[]{"a0 + a0 => a0 + a0"});
        parameters.add(new Object[]{"b0 + c0 => b0 + c0"});
        parameters.add(new Object[]{"a0a0 => a0 + a0"});
        parameters.add(new Object[]{"a0 + a0 => a0a0"});
        parameters.add(new Object[]{"a1 + a2 => a3 + a4"});
        parameters.add(new Object[]{"a0 + x0 => a0 + x0"});
        parameters.add(new Object[]{"a0 + y0 => a0 + y0"});
        parameters.add(new Object[]{"x0 + x0 => x0 + x0"});
        parameters.add(new Object[]{"y0 + y0 => y0 + y0"});
        parameters.add(new Object[]{"x0 + y0 => x0 + y0"});
        return parameters;
    }

    private final StringToReactionConverter converter = new StringToReactionConverter(
            new CharacterToReactionTypeConverter());

    private final String reactionString;

    public StringToReactionConverterSuccessTest(final String reactionString) {
        this.reactionString = reactionString;
    }

    @Test
    public void shouldParseReaction() {
        final Reaction reaction = converter.convert(reactionString);
        assertThat(reaction).isNotNull();
        assertThat(reaction.toString()).isEqualTo(reactionString);
    }

}
