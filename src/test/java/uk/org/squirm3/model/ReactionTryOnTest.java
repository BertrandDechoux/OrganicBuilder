package uk.org.squirm3.model;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.fest.assertions.Assertions.assertThat;
import static uk.org.squirm3.model.Atoms.createAtom;
import static uk.org.squirm3.model.type.def.BasicType.A;
import static uk.org.squirm3.model.type.def.BasicType.B;
import static uk.org.squirm3.model.type.def.WildcardType.X;
import static uk.org.squirm3.model.type.def.WildcardType.Y;

@RunWith(Parameterized.class)
public class ReactionTryOnTest {

    @Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> parameters = new ArrayList<Object[]>();
        parameters.add(new Object[]{
                new Reaction(A, 1, false, B, 2, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), true, 2, 3, true});
        parameters.add(new Object[]{
                new Reaction(A, 1, false, A, 2, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), false, 1, 2, false});
        parameters.add(new Object[]{
                new Reaction(B, 1, false, B, 2, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), false, 1, 2, false});
        parameters.add(new Object[]{
                new Reaction(A, 1, false, B, 1, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), false, 1, 2, false});
        parameters.add(new Object[]{
                new Reaction(A, 2, false, B, 2, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), false, 1, 2, false});
        parameters.add(new Object[]{new Reaction(A, 1, true, B, 2, 2, true, 3),
                createAtom(A, 1), createAtom(B, 2), false, 1, 2, false});
        parameters.add(new Object[]{
                new Reaction(X, 1, false, X, 2, 2, true, 3), createAtom(A, 1),
                createAtom(A, 2), true, 2, 3, true});
        parameters.add(new Object[]{
                new Reaction(X, 1, false, X, 2, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), false, 1, 2, false});
        parameters.add(new Object[]{
                new Reaction(Y, 1, false, Y, 2, 2, true, 3), createAtom(A, 1),
                createAtom(A, 2), true, 2, 3, true});
        parameters.add(new Object[]{
                new Reaction(Y, 1, false, Y, 2, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), false, 1, 2, false});
        parameters.add(new Object[]{
                new Reaction(X, 1, false, Y, 2, 2, true, 3), createAtom(A, 1),
                createAtom(A, 2), true, 2, 3, true});
        parameters.add(new Object[]{
                new Reaction(X, 1, false, Y, 2, 2, true, 3), createAtom(A, 1),
                createAtom(B, 2), true, 2, 3, true});
        return parameters;
    }

    private final Reaction reaction;
    private final Atom a;
    private final Atom b;
    private final boolean success;
    private final int aState, bState;
    private final boolean bonded;

    public ReactionTryOnTest(final Reaction reaction, final Atom a,
            final Atom b, final boolean success, final int aState,
            final int bState, final boolean bonded) {
        this.reaction = reaction;
        this.a = a;
        this.b = b;
        this.success = success;
        this.aState = aState;
        this.bState = bState;
        this.bonded = bonded;
    }

    @Test
    public void testTryOn() {
        assertThat(reaction.tryOn(a, b)).isEqualTo(success);
        assertThat(a.getState()).isEqualTo(aState);
        assertThat(b.getState()).isEqualTo(bState);
        assertThat(a.hasBondWith(b)).isEqualTo(bonded);
        assertThat(b.hasBondWith(a)).isEqualTo(bonded);
    }

}
