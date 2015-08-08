package uk.org.squirm3.springframework.converter;

import org.springframework.stereotype.Component;

import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.model.type.Types;

@Component
public class CharacterToBuilderTypeConverter
        extends
            CharacterToChemicalTypeConverter<BuilderType> {

    public CharacterToBuilderTypeConverter() {
        super(Types.getBuilderTypes());
    }

}
