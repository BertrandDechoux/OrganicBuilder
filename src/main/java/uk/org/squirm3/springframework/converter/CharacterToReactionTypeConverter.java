package uk.org.squirm3.springframework.converter;

import org.springframework.stereotype.Component;

import uk.org.squirm3.model.type.ReactionType;
import uk.org.squirm3.model.type.Types;

@Component
public class CharacterToReactionTypeConverter
        extends
            CharacterToChemicalTypeConverter<ReactionType> {

    public CharacterToReactionTypeConverter() {
        super(Types.getReactionTypes());
    }

}
