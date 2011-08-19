package uk.org.squirm3.springframework.converter;

import java.util.Collection;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.model.type.ChemicalType;
import uk.org.squirm3.model.type.ReactionType;

import com.google.common.collect.Maps;

/**
 * we could instantiate this class for {@link BuilderType} and
 * {@link ReactionType} but with inheritance spring will pickup automatically
 * the types used for the conversion mapping.
 */
public class CharacterToChemicalTypeConverter<T extends ChemicalType>
        implements
            Converter<Character, T> {

    private final Map<Character, T> chemicalTypeMapping = Maps.newHashMap();

    public CharacterToChemicalTypeConverter(
            final Collection<? extends T> convertedTypes) {
        for (final T type : convertedTypes) {
            chemicalTypeMapping.put(type.getCharacterIdentifier(), type);
        }
    }

    @Override
    public T convert(Character source) {
        return chemicalTypeMapping.get(source);
    }

}
