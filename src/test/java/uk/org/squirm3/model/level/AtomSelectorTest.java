package uk.org.squirm3.model.level;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.FixedPoint;

import com.google.common.collect.Lists;

import static org.fest.assertions.Assertions.assertThat;

public class AtomSelectorTest {
    private final Collection<? extends Atom> atoms = Lists.newArrayList(//
            new Atom(FixedPoint.ORIGIN, 0, 0),//
            new Atom(FixedPoint.ORIGIN, 0, 1),//
            new Atom(FixedPoint.ORIGIN, 1, 0),//
            new Atom(FixedPoint.ORIGIN, 1, 1),//
            new Atom(FixedPoint.ORIGIN, 1, 1));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldReturnNullWhenUnniqueNotFound() {
        assertThat(AtomSelector.findUnique("f0", atoms)).isNull();
    }

    @Test
    public void shouldReturnUnique() {
        assertThat(AtomSelector.findUnique("a0", atoms)).isNotNull();
    }

    @Test
    public void shouldFailWhenNotUnique() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("There are 2 atoms matching b1");

        AtomSelector.findUnique("b1", atoms);
    }

    @Test
    public void shouldFindAllMatchingAtoms() {
        assertThat(AtomSelector.findAll("b1", atoms)).hasSize(2);
    }

    @Test
    public void shouldFindNoMatchingAtoms() {
        assertThat(AtomSelector.findAll("f0", atoms)).isEmpty();
    }

}
