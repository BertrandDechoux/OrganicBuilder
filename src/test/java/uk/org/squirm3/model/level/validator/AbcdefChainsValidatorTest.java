package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.AbcdefChainsValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class AbcdefChainsValidatorTest extends ValidatorTest {
	@Test
	public void failWhenNoChain() {
		// given
		List<Atom> atoms = new ArrayList<>();
		addAbcdefChain(atoms, 1);
		addAbcdefChain(atoms, 2);
		addAbcdefChain(atoms, 3);
		addAbcdefChain(atoms, 4);
		addAbcdefChain(atoms, 5);
		addAbcdefChain(atoms, 7);
		addAbcdefLoop(atoms);
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	@Test
	public void failWhenSingleAbcdefChain() {
		// given
		List<Atom> atoms = new ArrayList<>();
		addAbcdefChain(atoms);
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_2);
	}

	@Test
	public void successWhenAbcdefChains() {
		// given
		List<Atom> atoms = new ArrayList<>();
		addAbcdefChain(atoms);
		addAbcdefChain(atoms);
		atoms.add(atom(BasicType.A));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	private void addAbcdefChain(List<Atom> atoms) {
		addAbcdefChain(atoms, 6);
	}

	private void addAbcdefChain(List<Atom> atoms, int size) {
		Atom a = atom(BasicType.A);
		Atom b = atom(BasicType.B);
		Atom c = atom(BasicType.C);
		Atom d = atom(BasicType.D);
		Atom e = atom(BasicType.E);
		Atom f = atom(BasicType.F);
		a.bondWith(b);
		b.bondWith(c);
		c.bondWith(d);
		d.bondWith(e);
		e.bondWith(f);
		if (size != 6)
			f.bondWith(atom(BasicType.D));
		atoms.addAll(Arrays.asList(a, b, c, d, e, f));
	}

	private void addAbcdefLoop(List<Atom> atoms) {
		Atom a = atom(BasicType.A);
		Atom b = atom(BasicType.B);
		Atom c = atom(BasicType.C);
		Atom d = atom(BasicType.D);
		Atom e = atom(BasicType.E);
		Atom f = atom(BasicType.F);
		a.bondWith(b);
		b.bondWith(c);
		c.bondWith(d);
		d.bondWith(e);
		e.bondWith(f);
		f.bondWith(a);
		atoms.addAll(Arrays.asList(a, b, c, d, e, f));
	}

	private String evaluate(List<Atom> atoms) {
		return new AbcdefChainsValidator().evaluate(atoms, messages);
	}
}
