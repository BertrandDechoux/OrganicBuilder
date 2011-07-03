package uk.org.squirm3.springframework.converter;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.RandomBasicType;
import uk.org.squirm3.model.type.def.RandomBuilderType;
import uk.org.squirm3.model.type.def.SpecialType;

import com.google.common.collect.Maps;

public class CharacterToBuilderTypeConverter
        implements
            Converter<Character, BuilderType> {

    private final Map<Character, BuilderType> builderTypeMapping = Maps
            .newHashMap();

    public CharacterToBuilderTypeConverter() {
        addBuilderTypesToMapping(BasicType.values());
        addBuilderTypesToMapping(RandomBasicType.values());
        addBuilderTypesToMapping(RandomBuilderType.values());
        addBuilderTypesToMapping(SpecialType.values());
    }

    private void addBuilderTypesToMapping(final BuilderType... builderTypes) {
        final int initialSize = builderTypeMapping.size();
        for (final BuilderType builderType : builderTypes) {
            builderTypeMapping.put(builderType.getCharacterIdentifier(),
                    builderType);
        }
        if (builderTypeMapping.size() != initialSize + builderTypes.length) {
            throw new IllegalStateException("At least two BuilderType instance have the same character identifier.");
        }
    }

    @Override
    public BuilderType convert(Character source) {
        return builderTypeMapping.get(source);
    }

}
