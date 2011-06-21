package uk.org.squirm3.model.type.def;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

import uk.org.squirm3.model.type.ChemicalType;

import com.google.common.collect.Sets;

import static org.fest.assertions.Assertions.assertThat;

public class TypeTest {

    @Test
    public void shouldHaveNoCollisions() {
        final int total = BasicType.values().length //
                + RandomBasicType.values().length //
                + RandomBuilderType.values().length //
                + SpecialType.values().length //
                + WildcardType.values().length;

        final Set<ChemicalType> chemicalTypes = Sets.newHashSet();
        chemicalTypes.addAll(Arrays.asList(BasicType.values()));
        chemicalTypes.addAll(Arrays.asList(RandomBasicType.values()));
        chemicalTypes.addAll(Arrays.asList(RandomBuilderType.values()));
        chemicalTypes.addAll(Arrays.asList(SpecialType.values()));
        chemicalTypes.addAll(Arrays.asList(WildcardType.values()));

        assertThat(chemicalTypes).hasSize(total);

        final Set<Character> characterIdentifiers = Sets.newHashSet();
        final Set<Integer> integerIdentifiers = Sets.newHashSet();

        for (final ChemicalType chemicalType : chemicalTypes) {
            characterIdentifiers.add(chemicalType.getCharacterIdentifier());
            integerIdentifiers.add(chemicalType.getIntegerIndentifier());
        }

        assertThat(characterIdentifiers).hasSize(total);
        assertThat(integerIdentifiers).hasSize(total);
    }

    @Test
    public void shouldExistOneRandomTypePerBasicType() {
        assertThat(RandomBasicType.values().length).isEqualTo(
                BasicType.values().length);
    }
}
