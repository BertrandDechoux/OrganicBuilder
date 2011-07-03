package uk.org.squirm3.engine.generator;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import static org.fest.assertions.Assertions.assertThat;

public class AtomBuilderTest {
    private final AtomBuilder levelBuilder = new AtomBuilder();

    private static final Configuration configuration = new Configuration(500f,
            500f);

    @Test
    public void shouldSupportEmptyDescription() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("", configuration);
        assertThat(atoms).isEmpty();
    }

    @Test
    public void shouldSupportEmptyLine() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("\n", configuration);
        assertThat(atoms).isEmpty();
    }

    @Test
    public void shouldSupportEmptyCell() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("......",
                configuration);
        assertThat(atoms).isEmpty();
    }

    @Test
    public void shouldSupportFixedAtom() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("[_a0_]",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().isStuck()).isTrue();
    }

    @Test
    public void shouldSupportMobileAtom() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("(_a0_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().isStuck()).isFalse();
    }

    @Test
    public void shouldSupportBasicType() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("(_a0_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getType()).isEqualTo(0);
        assertThat(atoms.iterator().next().getState()).isEqualTo(0);
    }

    @Test
    public void shouldSupportRandomBasicType() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("(_A1_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getState()).isEqualTo(1);
    }

    @Test
    public void shouldSupportRandomBuilderType() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("(_R1_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getState()).isEqualTo(1);
    }

    @Test
    public void shouldSupportSpecialType() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("(_K0_)",
                configuration);
        assertThat(atoms).hasSize(1);
        assertThat(atoms.iterator().next().getType()).isEqualTo(-1);
        assertThat(atoms.iterator().next().getState()).isEqualTo(0);
    }

    @Test
    public void shouldSupportHorizontalBond() throws BuilderException {
        final Collection<Atom> atoms = levelBuilder.build("(_a0_)(⇠b1_)",
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
        final Collection<Atom> atoms = levelBuilder.build("(_a0_)\n(_b1⇡)",
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
        final Collection<Atom> atoms = levelBuilder.build(
                "(_a0_)(_b1_)\n(_c3_)(_d4_)", configuration);
        assertThat(atoms).hasSize(4);
        for (final Atom atom : atoms) {
            assertThat(atom.getBonds()).isEmpty();
        }
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWithIncorrectAtomStart() throws BuilderException {
        levelBuilder.build("{_x2_}", configuration);
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWithIncorrectAtomStop() throws BuilderException {
        levelBuilder.build("(_x2_]", configuration);
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWithWildcardType() throws BuilderException {
        levelBuilder.build("(_x2_)", configuration);
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWithIncorrectAtomState() throws BuilderException {
        levelBuilder.build("(_aa_)", configuration);
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWithIncorrectHorizontalBonding()
            throws BuilderException {
        levelBuilder.build("(⇠a0_)", configuration);
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWithHorizontalBondingWithEmpty()
            throws BuilderException {
        levelBuilder.build("......(⇠a0_)", configuration);
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWithVerticalBondingWithEmpty()
            throws BuilderException {
        levelBuilder.build("......\n(_a0⇡)", configuration);
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWhenNotEnoughHorizontalSpace()
            throws BuilderException {
        levelBuilder.build("(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)(_a0_)",
                new Configuration(100, 500));
    }

    @Test(expected = BuilderException.class)
    public void shouldFailWhenNotEnoughVerticalSpace() throws BuilderException {
        levelBuilder.build(
                "(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n(_a0_)\n",
                new Configuration(500, 100));
    }

}
