package uk.org.squirm3.engine.generator;

import java.util.Collection;
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

    private static final Configuration configuration = new Configuration(500f,
            500f);

    @Before
    public void setup() {
        atomBuilder = new AtomBuilder(conversionService);
    }

    @Test
    public void shouldSupportEmptyDescription() throws BuilderException {
        final Collection<Atom> atoms = atomBuilder.build("", configuration);
        assertThat(atoms).isEmpty();
    }

    @Test
    public void shouldSupportEmptyLine() throws BuilderException {
        final Collection<Atom> atoms = atomBuilder.build("\n", configuration);
        assertThat(atoms).isEmpty();
    }

    @Test
    public void shouldSupportEmptyCell() throws BuilderException {
        final Collection<Atom> atoms = atomBuilder.build("......",
                configuration);
        assertThat(atoms).isEmpty();
    }

    @Test
    public void shouldSupportFixedAtom() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        final Collection<Atom> atoms = atomBuilder.build("[_a0_]",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().isStuck()).isTrue();
    }

    @Test
    public void shouldSupportMobileAtom() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        final Collection<Atom> atoms = atomBuilder.build("(_a0_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().isStuck()).isFalse();
    }

    @Test
    public void shouldSupportBasicType() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);

        final Collection<Atom> atoms = atomBuilder.build("(_a0_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getType()).isEqualTo(BasicType.A);
        assertThat(atoms.iterator().next().getState()).isEqualTo(0);
    }

    @Test
    public void shouldSupportRandomBasicType() throws BuilderException {
        when(conversionService.convert('A', BuilderType.class)).thenReturn(
                RandomBasicType.A);

        final Collection<Atom> atoms = atomBuilder.build("(_A1_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getState()).isEqualTo(1);
    }

    @Test
    public void shouldSupportRandomBuilderType() throws BuilderException {
        when(conversionService.convert('R', BuilderType.class)).thenReturn(
                RandomBuilderType.RANDOM);

        final Collection<Atom> atoms = atomBuilder.build("(_R1_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getState()).isEqualTo(1);
    }

    @Test
    public void shouldSupportSpecialType() throws BuilderException {
        when(conversionService.convert('K', BuilderType.class)).thenReturn(
                SpecialType.KILLER);

        final Collection<Atom> atoms = atomBuilder.build("(_K0_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getType()).isEqualTo(SpecialType.KILLER);
        assertThat(atoms.iterator().next().getState()).isEqualTo(0);
    }

    @Test
    public void shouldSupportHorizontalBond() throws BuilderException {
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        when(conversionService.convert('b', BuilderType.class)).thenReturn(
                BasicType.B);

        final Collection<Atom> atoms = atomBuilder.build("(_a0_)(⇠b1_)",
                configuration);
        assertThat(atoms).hasSize(2);
        final Iterator<Atom> atomIterator = atoms.iterator();
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

        final Collection<Atom> atoms = atomBuilder.build("(_a0_)\n(_b1⇡)",
                configuration);
        assertThat(atoms).hasSize(2);
        final Iterator<Atom> atomIterator = atoms.iterator();
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

        final Collection<Atom> atoms = atomBuilder.build(
                "(_a0_)(_b1_)\n(_c3_)(_d4_)", configuration);
        assertThat(atoms).hasSize(4);
        for (final Atom atom : atoms) {
            assertThat(atom.getBonds()).isEmpty();
        }
    }

    @Test
    public void shouldFailWithIncorrectAtomStart() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Illegal description of atom : {_x2_}");
        
        atomBuilder.build("{_x2_}", configuration);
    }

    @Test
    public void shouldFailWithIncorrectAtomStop() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Illegal end of mobile atom : (_x2_]");
        
        atomBuilder.build("(_x2_]", configuration);
    }

    @Test
    public void shouldFailWithWildcardType() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect BuilderType : x");
        
        atomBuilder.build("(_x2_)", configuration);
    }

    @Test
    public void shouldFailWithIncorrectAtomState() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect state : a");
        
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        
        atomBuilder.build("(_aa_)", configuration);
    }

    @Test
    public void shouldFailWithIncorrectHorizontalBonding()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect setting for horizontal bond : there is no previous atom!");
        
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        
        atomBuilder.build("(⇠a0_)", configuration);
    }

    @Test
    public void shouldFailWithHorizontalBondingWithEmpty()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect setting for horizontal bond : there is no previous atom!");
        
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        
        atomBuilder.build("......(⇠a0_)", configuration);
    }

    @Test
    public void shouldFailWithVerticalBondingWithEmpty()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Incorrect setting for vertical bond : there is no upper atom!");
        
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        
        atomBuilder.build("......\n(_a0⇡)", configuration);
    }

    @Test
    public void shouldFailWhenNotEnoughHorizontalSpace()
            throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Map horizontal space is greater than the configuration's width : 374.0 > 100.0");
        
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        
        atomBuilder.build("(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)",
                new Configuration(100, 500));
    }

    @Test
    public void shouldFailWhenNotEnoughVerticalSpace() throws BuilderException {
        thrown.expect(BuilderException.class);
        thrown.expectMessage("Map vertical space is greater than the configuration's height : 330.0 > 100.0");
        
        when(conversionService.convert('a', BuilderType.class)).thenReturn(
                BasicType.A);
        
        atomBuilder.build(
                "(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n",
                new Configuration(500, 100));
    }

}
