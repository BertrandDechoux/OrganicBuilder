package uk.org.squirm3.model.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Sets;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TypesTest {

    private final Collection<? extends ChemicalType> chemicalTypes;
    private final int expectedSize;
    final Set<Character> characterIdentifiers = Sets.newHashSet();

    public TypesTest(Collection<? extends ChemicalType> chemicalTypes,
            final int expectedSize) {
        this.chemicalTypes = chemicalTypes;
        this.expectedSize = expectedSize;

        for (final ChemicalType chemicalType : chemicalTypes) {
            characterIdentifiers.add(chemicalType.getCharacterIdentifier());
        }
    }
    
    @Parameters
    public static Collection<Object[]> getParameters() {
        final Collection<Object[]> parameters = new ArrayList<Object[]>();
        parameters.add(new Object[] {Types.getAtomTypes(), 7});
        parameters.add(new Object[] {Types.getReactionTypes(), 8});
        parameters.add(new Object[] {Types.getBuilderTypes(), 14});
        parameters.add(new Object[] {Types.getChemicalTypes(), 16});
        return parameters;
    }

    @Test
    public void shouldHaveNoCharacterIdentifierCollision() {
        assertThat(characterIdentifiers).hasSize(chemicalTypes.size());
    }

    @Test
    public void shouldNotBeChangedRecklessly() {
        Assertions.assertThat(chemicalTypes).hasSize(expectedSize);
    }

}
