package uk.org.squirm3.model.level;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Atoms;
import uk.org.squirm3.model.type.def.BasicType;

import com.google.common.collect.Lists;

import static org.fest.assertions.Assertions.assertThat;

public class AtomSelectorTest {
    private final Collection<? extends Atom> atoms = Lists.newArrayList(//
            Atoms.createAtom(BasicType.A, 0),//
            Atoms.createAtom(BasicType.A, 1),//
            Atoms.createAtom(BasicType.B, 0),//
            Atoms.createAtom(BasicType.B, 1), //
            Atoms.createAtom(BasicType.B, 1));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnNullWhenUniqueNotFound() {
        assertThat(AtomSelector.findUnique(BasicType.F, 0, atoms)).isNull();
    }

    @Test
    public void shouldReturnUnique() {
        assertThat(AtomSelector.findUnique(BasicType.A, 0, atoms)).isNotNull();
    }

    @Test
    public void shouldFailWhenNotUnique() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("There are 2 atoms matching b1");

        AtomSelector.findUnique(BasicType.B, 1, atoms);
    }

    @Test
    public void shouldFindAllMatchingAtoms() {
        assertThat(AtomSelector.findAll(BasicType.B, 1, atoms)).hasSize(2);
    }

    @Test
    public void shouldFindNoMatchingAtoms() {
        assertThat(AtomSelector.findAll(BasicType.F, 0, atoms)).isEmpty();
    }

}
