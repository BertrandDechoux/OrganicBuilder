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
	private static final BasicType EUROPIUM = BasicType.E;
	private static final BasicType CARBON = BasicType.C;

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
		atoms.add(atom(CARBON));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void successWhenSingleEuropium() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(EUROPIUM));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void successWhenEuropiumCarbonPair() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom e = atom(EUROPIUM);
		Atom c = atom(CARBON);
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
		Atom e1 = atom(EUROPIUM);
		Atom c = atom(CARBON);
		Atom e2 = atom(EUROPIUM);
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
		Atom e = atom(EUROPIUM);
		Atom c = atom(CARBON);
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
		return new PairValidator(EUROPIUM, CARBON).evaluate(atoms, messages);
	}
}
