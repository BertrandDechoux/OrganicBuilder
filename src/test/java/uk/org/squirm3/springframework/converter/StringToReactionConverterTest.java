package uk.org.squirm3.springframework.converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.model.type.ReactionType;
import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.WildcardType;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringToReactionConverterTest {

    @Mock
    private Converter<Character, ReactionType> characterToReactionTypeConverter;

    private Converter<String, Reaction> stringToReactionConverter;

    @Before
    public void setup() {
        stringToReactionConverter = new StringToReactionConverter(
                characterToReactionTypeConverter);
    }

    @Test
    public void shouldCreateTwoObjetsWithSameDescription() {
        mapCharacterToReactionType('b', BasicType.B);
        mapCharacterToReactionType('d', BasicType.D);

        Reaction originalReaction = new Reaction(BasicType.B, 2, false, BasicType.D, 4, 5, true, 6);
        Reaction parsedReaction = stringToReactionConverter
                .convert(originalReaction.toString());
        assertThat(originalReaction.toString()).isEqualTo(
                parsedReaction.toString());
    }

    @Test
    public void shouldParseItsOwnDescription() {
        mapCharacterToReactionType('x', WildcardType.X);
        mapCharacterToReactionType('y', WildcardType.Y);

        Reaction originalReaction = new Reaction(WildcardType.X, 20345, true, WildcardType.Y, 4234, 5,
                false, 6);
        Reaction parsedReaction = stringToReactionConverter
                .convert("x20345y4234-> \tx5   +  y6");
        assertThat(originalReaction.toString()).isEqualTo(
                parsedReaction.toString());
    }
    
    private void mapCharacterToReactionType(char stringType,
            ReactionType objectType) {
        when(characterToReactionTypeConverter.convert(stringType)).thenReturn(
                objectType);
    }

}
