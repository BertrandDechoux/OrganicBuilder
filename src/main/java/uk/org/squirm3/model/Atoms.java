package uk.org.squirm3.model;

import uk.org.squirm3.model.type.AtomType;

public abstract class Atoms {

	public static Atom createAtom(AtomType type, int state) {
		return new Atom(FixedPoint.ORIGIN, type, state);
	}

	public static Atom createFixedAtom(AtomType type, int state, double x, double y) {
		return new Atom(new FixedPoint(x, y), type, state);
	}

	public static Atom createMobileAtomWithRandomSpeed(AtomType type, int state, double x, double y) {
		double ms = Atom.getAtomSize() / 3;
		double dx = Math.random() * ms - ms / 2.0;
		double dy = Math.random() * ms - ms / 2.0;
		return new Atom(new MobilePoint(x, y, dx, dy, 0, 0), type, state);
	}

	public static Atom createAtom(AtomType type, int state, double x, double y, boolean fixed) {
		if (fixed) {
			return createFixedAtom(type, state, x, y);
		}
		return createMobileAtomWithRandomSpeed(type, state, x, y);
	}

}
