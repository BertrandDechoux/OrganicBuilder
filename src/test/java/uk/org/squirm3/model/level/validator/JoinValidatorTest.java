package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.JoinValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class JoinValidatorTest extends ValidatorTest {
	private static final BasicType ALUMINIUM = BasicType.A;

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
	public void successWhenSingleAluminium() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(ALUMINIUM));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void successWhenBondedAluminiums() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a1 = atom(ALUMINIUM);
		Atom a2 = atom(ALUMINIUM);
		Atom a3 = atom(ALUMINIUM);
		Atom b = atom(BasicType.B);
		Atom c = atom(BasicType.C);
		a1.bondWith(a2);
		a2.bondWith(a3);
		atoms.addAll(Arrays.asList(a1, b, a2, c, a3));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	@Test
	public void failWhenOtherAtomHasBond() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(ALUMINIUM));
		Atom b = atom(BasicType.B);
		Atom c = atom(BasicType.C);
		b.bondWith(c);
		atoms.add(b);
		atoms.add(c);
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	@Test
	public void failWhenLooseAluminium() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(atom(ALUMINIUM));
		atoms.add(atom(ALUMINIUM));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_2);
	}

	@Test
	public void failWhenIsolatedAluminiums() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a1 = atom(ALUMINIUM);
		Atom a2 = atom(ALUMINIUM);
		Atom a3 = atom(ALUMINIUM);
		Atom a4 = atom(ALUMINIUM);
		a1.bondWith(a2);
		a3.bondWith(a4);
		atoms.addAll(Arrays.asList(a1, a2, a3, a4));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_2);
	}

	private String evaluate(List<Atom> atoms) {
		return new JoinValidator(ALUMINIUM).evaluate(atoms, messages);
	}
}
