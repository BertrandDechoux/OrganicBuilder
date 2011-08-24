package uk.org.squirm3.engine.generator;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.RandomBasicType;
import uk.org.squirm3.model.type.def.RandomBuilderType;
import uk.org.squirm3.model.type.def.SpecialType;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AtomBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ConversionService conversionService;
    private AtomBuilder atomBuilder;

    @Before
    public void setup() {
        atomBuilder = new AtomBuilder(conversionService);
    }

    @Test
    public void shouldFirstLineConfigureSize() throws BuilderException {
        final Configuration configuration = atomBuilder.build("#13x13");
        assertThat(configuration).isNotNull();
        assertThat(configuration.getAtoms()).isEmpty();
    }

    @Test
    public void shouldSupportEmptyLine() throws BuilderException {
        final Configuration configuration = atomBuilder.build("#13x13\n\n");
        assertThat(configuration.getAtoms()).isEmpty();
    }

    @Test
    public void shouldSupportEmptyCell() throws BuilderException {
        final Configuration configuration = atomBuilder.build("#13x13\n......");
        assertThat(configuration.getAtoms()).isEmpty();
    }

    @Test
    public void shouldSupportFixedAtom() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        final Configuration configuration = atomBuilder.build("#13x13\n[_a0_]");
        assertThat(configuration.getAtoms()).hasSize(1);
        assertThat(configuration.getAtoms().iterator().next().isStuck())
                .isTrue();
    }

    @Test
    public void shouldSupportMobileAtom() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        final Configuration configuration = atomBuilder.build("#13x13\n(_a0_)");
        assertThat(configuration.getAtoms()).hasSize(1);
        assertThat(configuration.getAtoms().iterator().next().isStuck())
                .isFalse();
    }

    @Test
    public void shouldSupportBasicType() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        final Configuration configuration = atomBuilder.build("#13x13\n(_a0_)");
        assertThat(configuration.getAtoms()).hasSize(1);
        assertThat(configuration.getAtoms().iterator().next().getType())
                .isEqualTo(BasicType.A);
        assertThat(configuration.getAtoms().iterator().next().getState())
                .isEqualTo(0);
    }

    @Test
    public void shouldSupportRandomBasicType() throws BuilderException {
        when(conversionService.convert('A', BuilderType.class)).thenReturn(
                RandomBasicType.A);

        final Configuration configuration = atomBuilder.build("#13x13\n(_A1_)");
        assertThat(configuration.getAtoms()).hasSize(1);
        assertThat(configuration.getAtoms().iterator().next().getState())
                .isEqualTo(1);
    }

    @Test
    public void shouldSupportRandomBuilderType() throws BuilderException {
        when(conversionService.convert('R', BuilderType.class)).thenReturn(
                RandomBuilderType.RANDOM);

        final Configuration configuration = atomBuilder.build("#13x13\n(_R1_)");
        assertThat(configuration.getAtoms()).hasSize(1);
        assertThat(configuration.getAtoms().iterator().next().getState())
                .isEqualTo(1);
    }

    @Test
    public void shouldSupportSpecialType() throws BuilderException {
        when(conversionService.convert('K', BuilderType.class)).thenReturn(
                SpecialType.KILLER);

        final Configuration configuration = atomBuilder.build("#13x13\n(_K0_)");
        assertThat(configuration.getAtoms()).hasSize(1);
        assertThat(configuration.getAtoms().iterator().next().getType())
                .isEqualTo(SpecialType.KILLER);
        assertThat(configuration.getAtoms().iterator().next().getState())
                .isEqualTo(0);
    }

    @Test
    public void shouldSupportHorizontalBond() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        when(conversionService.convert('b', BuilderType.class)).thenReturn(
                BasicType.B);

        final Configuration configuration = atomBuilder
                .build("#13x13\n(_a0_)(⇠b1_)");
        assertThat(configuration.getAtoms()).hasSize(2);
        final Iterator<Atom> atomIterator = configuration.getAtoms().iterator();
        final Atom firstAtom = atomIterator.next();
        final Atom secondAtom = atomIterator.next();
        assertThat(firstAtom.getBonds()).hasSize(1);
        assertThat(firstAtom.getBonds()).contains(secondAtom);
        assertThat(secondAtom.getBonds()).hasSize(1);
        assertThat(secondAtom.getBonds()).contains(firstAtom);
    }

    @Test
    public void shouldSupportVerticalBond() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        when(conversionService.convert('b', BuilderType.class)).thenReturn(
                BasicType.B);

        final Configuration configuration = atomBuilder
                .build("#13x13\n(_a0_)\n(_b1⇡)");
        assertThat(configuration.getAtoms()).hasSize(2);
        final Iterator<Atom> atomIterator = configuration.getAtoms().iterator();
        final Atom firstAtom = atomIterator.next();
        final Atom secondAtom = atomIterator.next();
        assertThat(firstAtom.getBonds()).hasSize(1);
        assertThat(firstAtom.getBonds()).contains(secondAtom);
        assertThat(secondAtom.getBonds()).hasSize(1);
        assertThat(secondAtom.getBonds()).contains(firstAtom);
    }

    @Test
    public void shouldNotBondWhenNotRequested() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        when(conversionService.convert('b', BuilderType.class)).thenReturn(
                BasicType.B);
        when(conversionService.convert('c', BuilderType.class)).thenReturn(
                BasicType.C);
        when(conversionService.convert('d', BuilderType.class)).thenReturn(
                BasicType.D);

        final Configuration configuration = atomBuilder
                .build("#13x13\n(_a0_)(_b1_)\n(_c3_)(_d4_)");
        assertThat(configuration.getAtoms()).hasSize(4);
        for (final Atom atom : configuration.getAtoms()) {
            assertThat(atom.getBonds()).isEmpty();
        }
    }

    @Test
    public void shouldFailIfFirstLineIsNoSizeConfiguration()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("First line should indicate the size of the level : lol");

        atomBuilder.build("lol");
    }

    @Test
    public void shouldFailWithIncorrectAtomStart() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Illegal description of atom : {_x2_}");

        atomBuilder.build("#13x13\n{_x2_}");
    }

    @Test
    public void shouldFailWithIncorrectAtomStop() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Illegal end of mobile atom : (_x2_]");

        atomBuilder.build("#13x13\n(_x2_]");
    }

    @Test
    public void shouldFailWithWildcardType() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect BuilderType : x");

        atomBuilder.build("#13x13\n(_x2_)");
    }

    @Test
    public void shouldFailWithIncorrectAtomState() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect state : a");

        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        atomBuilder.build("#13x13\n(_aa_)");
    }

    @Test
    public void shouldFailWithIncorrectHorizontalBonding()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect setting for horizontal bond : there is no previous atom!");

        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        atomBuilder.build("#13x13\n(⇠a0_)");
    }

    @Test
    public void shouldFailWithHorizontalBondingWithEmpty()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect setting for horizontal bond : there is no previous atom!");

        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        atomBuilder.build("#13x13\n......(⇠a0_)");
    }

    @Test
    public void shouldFailWithVerticalBondingWithEmpty()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect setting for vertical bond : there is no upper atom!");

        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        atomBuilder.build("#13x13\n......\n(_a0⇡)");
    }

    @Test
    public void shouldFailWhenNotEnoughHorizontalSpace()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Map horizontal space is greater than the configuration's width : 374.0 > 44.0");

        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        atomBuilder.build("#1x13\n(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)");
    }

    @Test
    public void shouldFailWhenNotEnoughVerticalSpace() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Map vertical space is greater than the configuration's height : 330.0 > 44.0");

        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        atomBuilder
                .build("#13x1\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n");
    }

}
