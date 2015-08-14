package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.PairValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class PairValidatorTest extends ValidatorTest {

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
	public void successWhenSingleCarbon() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(BasicType.C));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void successWhenSingleEuropium() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(BasicType.E));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void successWhenEuropiumCarbonPair() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom e = atom(BasicType.E);
		Atom c = atom(BasicType.C);
		Atom a = atom(BasicType.A);
		Atom b = atom(BasicType.B);
		e.bondWith(c);
		atoms.addAll(Arrays.asList(e, b, a, c));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void failWhenEuropiumCarbonStructure() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom e1 = atom(BasicType.E);
		Atom c = atom(BasicType.C);
		Atom e2 = atom(BasicType.E);
		e1.bondWith(c);
		e2.bondWith(c);
		atoms.addAll(Arrays.asList(e1, c, e2));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_2);
	}

	@Test
	public void failWhenUnbondedEuropiumCarbon() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom e = atom(BasicType.E);
		Atom c = atom(BasicType.C);
		atoms.addAll(Arrays.asList(e, c));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_3);
	}

	@Test
	public void failWhenOtherAtomHasBond() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(BasicType.A));
		Atom b = atom(BasicType.B);
		Atom d = atom(BasicType.D);
		b.bondWith(d);
		atoms.addAll(Arrays.asList(b, d));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	private String evaluate(List<Atom> atoms) {
		return new PairValidator(BasicType.E, BasicType.C).evaluate(atoms, messages);
	}
}
