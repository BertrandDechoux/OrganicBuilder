package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.JoinSameValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class JoinSameValidatorTest extends ValidatorTest {
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
	public void successWhenSingleAtomOfEachType() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(BasicType.A));
		atoms.add(atom(BasicType.B));
		atoms.add(atom(BasicType.C));
		atoms.add(atom(BasicType.D));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void failWhenBondBetweenDifferentType() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a = atom(BasicType.A);
		Atom b = atom(BasicType.B);
		a.bondWith(b);
		atoms.addAll(Arrays.asList(a, b));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}
	
	@Test
	public void failWhenLooseAtom() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a1 = atom(BasicType.A);
		Atom a2 = atom(BasicType.A);
		atoms.addAll(Arrays.asList(a1, a2));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_2);
	}

	@Test
	public void successWhenAllAtomsBondedByType() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a1 = atom(BasicType.A);
		Atom a2 = atom(BasicType.A);
		a1.bondWith(a2);
		Atom b1 = atom(BasicType.B);
		Atom b2 = atom(BasicType.B);
		b1.bondWith(b2);
		atoms.addAll(Arrays.asList(a1, a2, b1, b2));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}


	private String evaluate(List<Atom> atoms) {
		return new JoinSameValidator().evaluate(atoms, messages);
	}
}
