package uk.org.squirm3.springframework.converter;

import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.model.type.Types;

public class CharacterToReactionTypeConverter
        extends CharacterToChemicalTypeConverter<BuilderType> {

    public CharacterToReactionTypeConverter() {
        super(Types.getBuilderTypes());
    }


}
