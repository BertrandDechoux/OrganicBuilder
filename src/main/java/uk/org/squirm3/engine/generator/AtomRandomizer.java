package uk.org.squirm3.engine.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.IPhysicalPoint;
import uk.org.squirm3.model.type.ChemicalType;
import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.RandomBasicType;
import uk.org.squirm3.model.type.def.RandomBuilderType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AtomRandomizer {

    private final Map<RandomBasicType, BasicType> typeMapping = Maps
            .newEnumMap(RandomBasicType.class);
    private final List<BasicType> basicTypes = Lists.newArrayList(BasicType
            .values());

    public AtomRandomizer() {
        final List<BasicType> basicTypes = Arrays.asList(BasicType.values());
        Collections.shuffle(basicTypes);

        int index = 0;
        for (final RandomBasicType randomType : RandomBasicType.values()) {
            typeMapping.put(randomType, BasicType.values()[index++]);
        }
    }

    public void setRandomSpeed(final IPhysicalPoint physicalPoint) {
        final float ms = Atom.getAtomSize() / 3;
        physicalPoint.setSpeedX((float) (Math.random() * ms - ms / 2.0));
        physicalPoint.setSpeedY((float) (Math.random() * ms - ms / 2.0));

    }

    public int getIntegerIdentifier(final RandomBuilderType randomType,
            final String configuration) {
        final List<? extends ChemicalType> types = Lists
                .newArrayList(basicTypes);

        Collections.shuffle(types);
        ChemicalType type = types.iterator().next();
        while (configuration.indexOf(type.getCharacterIdentifier()) == -1) {
            type = types.iterator().next();
            types.remove(type);
        }
        return type.getIntegerIndentifier();
    }

    public int getIntegerIdentifier(final RandomBasicType randomType) {
        return typeMapping.get(randomType).getIntegerIndentifier();
    }

}
