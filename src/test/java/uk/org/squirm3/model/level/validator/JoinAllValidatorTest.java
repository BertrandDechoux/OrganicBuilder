package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.JoinAllValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class JoinAllValidatorTest extends ValidatorTest {
	@Test
	public void successWhenNoAtom() {
		// given
		List<Atom> atoms = new ArrayList<>();
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void successWhenSingleAtom() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(BasicType.A));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void successWhenBondedAtoms() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a = atom(BasicType.A);
		Atom b = atom(BasicType.B);
		Atom c = atom(BasicType.C);
		a.bondWith(b);
		b.bondWith(c);
		atoms.addAll(Arrays.asList(a, b, c));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void failWhenLooseAtoms() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a = atom(BasicType.A);
		Atom b = atom(BasicType.B);
		Atom c = atom(BasicType.C);
		atoms.addAll(Arrays.asList(a, b, c));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	private String evaluate(List<Atom> atoms) {
		return new JoinAllValidator().evaluate(atoms, messages);
	}
}
