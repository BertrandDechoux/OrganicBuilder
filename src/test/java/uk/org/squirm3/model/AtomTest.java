package uk.org.squirm3.model;

import java.util.LinkedList;

import org.junit.Test;

import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.SpecialType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AtomTest {

    /**
     * Test of the constructor. *
     */
    @Test
    public void testAtom() {
        final Atom a = Atoms.createAtom(BasicType.D, 5);
        assertTrue(a.getType() == BasicType.D);
        assertTrue(a.getState() == 5);
    }

    /**
     * Test de la propriete stuck. *
     */
    @Test
    public void testStuck() {
        assertFalse(Atoms.createMobileAtomWithRandomSpeed(BasicType.A, 0, 0, 0).isStuck());
        assertTrue(Atoms.createFixedAtom(BasicType.A, 0, 0, 0).isStuck());
    }

    /**
     * Test de la propriete killer. *
     */
    @Test
    public void testKiller() {
        assertFalse(Atoms.createAtom(BasicType.A, 0).isKiller());
        assertTrue(Atoms.createAtom(SpecialType.KILLER, 0).isKiller());
    }

    /**
     * Test of basic bonding and breaking.
     */
    @Test
    public void testBondingAndBreaking() {
        final Atom a1 = Atoms.createAtom(BasicType.A, 1);
        final Atom a2 = Atoms.createAtom(BasicType.A, 2);
        assertFalse(a1.hasBondWith(a2));
        assertFalse(a2.hasBondWith(a1));

        a1.bondWith(a2);
        assertTrue(a1.hasBondWith(a2));
        assertTrue(a2.hasBondWith(a1));

        a1.breakBondWith(a2);
        assertFalse(a1.hasBondWith(a2));
        assertFalse(a2.hasBondWith(a1));
    }

    /**
     * Test of breakAllBonds() and getAllConnectedAtoms(). *
     */
    @Test
    public void testGeneralMethods() {
        final Atom a1 = Atoms.createAtom(BasicType.A, 1);
        final Atom a2 = Atoms.createAtom(BasicType.A, 2);
        final Atom a3 = Atoms.createAtom(BasicType.A, 3);;
        final Atom a4 = Atoms.createAtom(BasicType.A, 4);
        final Atom a5 = Atoms.createAtom(BasicType.A, 3);
        final Atom a6 = Atoms.createAtom(BasicType.A, 4);

        a1.bondWith(a2);
        a1.bondWith(a3);
        a1.bondWith(a6);
        a3.bondWith(a4);
        a3.bondWith(a5);
        a5.bondWith(a6);

        final LinkedList<Atom> l = new LinkedList<Atom>();
        a1.getAllConnectedAtoms(l);

        assertTrue(l.contains(a1));
        assertTrue(l.contains(a2));
        assertTrue(l.contains(a3));
        assertTrue(l.contains(a4));
        assertTrue(l.contains(a5));
        assertTrue(l.contains(a6));

        a1.breakAllBonds();
        l.clear();
        a1.getAllConnectedAtoms(l);
        assertTrue(l.contains(a1));
        l.remove(a1);
        assertTrue(l.isEmpty());
    }

    /**
     * Test of the string representation. *
     */
    @Test
    public void testToString() {
        final Atom a = Atoms.createAtom(BasicType.B, 1);
        assertTrue("b1".equals(a.toString()));
    }
}
