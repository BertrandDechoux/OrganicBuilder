package uk.org.squirm3.springframework.converter;

import uk.org.squirm3.model.type.ReactionType;
import uk.org.squirm3.model.type.Types;

public class CharacterToReactionTypeConverter
        extends CharacterToChemicalTypeConverter<ReactionType> {

    public CharacterToReactionTypeConverter() {
        super(Types.getReactionTypes());
    }


}
