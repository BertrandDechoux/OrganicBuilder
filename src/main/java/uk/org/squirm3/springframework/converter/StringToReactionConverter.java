package uk.org.squirm3.springframework.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.model.type.ReactionType;
import uk.org.squirm3.springframework.util.StringUtils;

public class StringToReactionConverter implements Converter<String, Reaction> {
    // the basic parts of a reaction pattern
    private static final String ATOM = "(\\p{Lower})(\\d{1,})";
    private static final String BOND = "(\\+?)";

    // the three main parts of a reaction pattern
    private static final String EVENT = "^" + ATOM + BOND + ATOM;
    private static final String TO = "[=\\-]>";
    private static final String RESULT = //
    "\\1(\\d{1,})" + BOND + "\\4(\\d{1,})$";

    // full reaction pattern
    private static final String REACTION = EVENT + TO + RESULT;

    private static final Pattern pattern = Pattern.compile(REACTION);

    private final Converter<Character, ReactionType> characterToReactionTypeConverter;

    public StringToReactionConverter(
            final Converter<Character, ReactionType> characterToReactionTypeConverter) {
        this.characterToReactionTypeConverter = characterToReactionTypeConverter;
    }

    @Override
    public Reaction convert(String source) {
        final Matcher m = match(source);
        final ReactionType a_type = toReactionType(m.group(1));
        final int a_state = toState(m.group(2));
        final boolean bonded_before = isBonded(m.group(3));
        final ReactionType b_type = toReactionType(m.group(4));
        final int b_state = toState(m.group(5));
        final int future_a_state = toState(m.group(6));
        final boolean bonded_after = isBonded(m.group(7));
        final int future_b_state = toState(m.group(8));
        return new Reaction(a_type, a_state, bonded_before, b_type, b_state,
                future_a_state, bonded_after, future_b_state);
    }

    private Matcher match(String source) {
        final Matcher m = pattern
                .matcher(StringUtils.removeWhitespaces(source));
        m.matches();
        return m;
    }

    private int toState(String group) {
        return Integer.parseInt(group);
    }

    private boolean isBonded(String group) {
        return !group.contains("+");
    }

    private ReactionType toReactionType(String group) {
        return characterToReactionTypeConverter.convert(group.charAt(0));
    }

}
