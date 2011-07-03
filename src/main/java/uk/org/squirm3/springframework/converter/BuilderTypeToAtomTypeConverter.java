package uk.org.squirm3.springframework.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.RandomBasicType;
import uk.org.squirm3.model.type.def.RandomBuilderType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BuilderTypeToAtomTypeConverter
        implements
            Converter<BuilderType, AtomType> {

    private final Map<RandomBasicType, BasicType> basicTypeMapping = Maps
            .newEnumMap(RandomBasicType.class);
    private final List<BasicType> basicTypes = Lists.newArrayList(BasicType
            .values());

    private final String configuration;

    public BuilderTypeToAtomTypeConverter(final String configuration) {
        final List<BasicType> basicTypes = Arrays.asList(BasicType.values());
        Collections.shuffle(basicTypes);

        int index = 0;
        for (final RandomBasicType randomType : RandomBasicType.values()) {
            basicTypeMapping.put(randomType, basicTypes.get(index++));
        }

        this.configuration = configuration;
    }

    @Override
    public AtomType convert(BuilderType source) {
        if (source instanceof AtomType) {
            return (AtomType) source;
        }
        if (source instanceof RandomBasicType) {
            return basicTypeMapping.get(source);
        }

        if (!(source instanceof RandomBuilderType)) {
            throw new IllegalStateException("Unexpected type of BuilderType "
                    + source.getClass().getCanonicalName());
        }

        return findRandomAtomType();
    }

    private AtomType findRandomAtomType() {
        final List<? extends AtomType> types = Lists.newArrayList(basicTypes);
        Collections.shuffle(types);
        
        AtomType type = types.iterator().next();
        while (configuration.indexOf(type.getCharacterIdentifier()) == -1) {
            type = types.iterator().next();
            types.remove(type);
        }
        
        return type;
    }

}
