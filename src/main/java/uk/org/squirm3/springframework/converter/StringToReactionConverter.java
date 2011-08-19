package uk.org.squirm3.springframework.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.model.type.ReactionType;

public class StringToReactionConverter implements Converter<String, Reaction> {

    private static final String types = "abcdefxy";
    private static final Pattern pattern = Pattern.compile("([" + types
            + "])(\\d{1,})" + "(\\s*\\+\\s*|\\s*)" + "([" + types
            + "])(\\d{1,})" + "\\s*[=\\-]>\\s*" + "\\1(\\d{1,})"
            + "(\\s*\\+\\s*|\\s*)" + "\\4(\\d{1,})$");

    private final Converter<Character, ReactionType> characterToReactionTypeConverter;

    public StringToReactionConverter(final Converter<Character, ReactionType> characterToReactionTypeConverter) {
        this.characterToReactionTypeConverter = characterToReactionTypeConverter;
    }

    @Override
    public Reaction convert(String source) {
        final Matcher m = pattern.matcher(source);
        final boolean b = m.matches();
        if (!b) {
            return null;
        }
        final ReactionType aReactionType = characterToReactionTypeConverter.convert(m.group(1).charAt(0));
        if(aReactionType == null) {
            return null;
        }
        final int a_type = aReactionType.getIntegerIndentifier();
        
        final int a_state = Integer.parseInt(m.group(2));
        final boolean bonded_before = !m.group(3).contains("+");
        
        final ReactionType bReactionType = characterToReactionTypeConverter.convert(m.group(4).charAt(0));
        if(bReactionType == null) {
            return null;
        }
        final int b_type = bReactionType.getIntegerIndentifier();
        
        final int b_state = Integer.parseInt(m.group(5));
        final int future_a_state = Integer.parseInt(m.group(6));
        final boolean bonded_after = !m.group(7).contains("+");
        final int future_b_state = Integer.parseInt(m.group(8));
        final Reaction r = new Reaction(a_type, a_state, bonded_before, b_type,
                b_state, future_a_state, bonded_after, future_b_state);
        return r;
    }

}
