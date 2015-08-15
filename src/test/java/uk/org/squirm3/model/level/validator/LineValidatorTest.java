package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.LineValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class LineValidatorTest extends ValidatorTest {
	private static final BasicType CARBON = BasicType.C;
	private static final int SEED_STATE = 1;
	private Atom seed;
	
	@Before
	public void setup() {
		super.setup();
		seed = atom(CARBON, SEED_STATE);
	}
	
    @Rule
    public ExpectedException thrown = ExpectedException.none();

	@Test
	public void failWhenSeedIsNotFound() {
		// expect
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Seed not found.");
		
		// given
		Collection<Atom> atoms = Arrays.asList(//
				atom(CARBON, SEED_STATE + 1),//
				atom(BasicType.A));
		
		// when
		new LineValidator(CARBON, SEED_STATE).setup(atoms);
	}

	@Test
	public void failWhenNoLineFound() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.addAll(Arrays.asList(seed, atom(BasicType.A)));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_2);
	}

	@Test
	public void failWhenOnlyAdditionalLooseCarbon() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.addAll(Arrays.asList(seed, atom(BasicType.C)));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_2);
	}
	
	@Test
	public void successWhenCarbonLine() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom c1 = atom(CARBON);
		Atom c2 = atom(CARBON);
		Atom a = atom(BasicType.A);
		seed.bondWith(c1);
		c1.bondWith(c2);
		atoms.addAll(Arrays.asList(seed, a, c2, c1));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}
	
	@Test
	public void successWhenCarbonLoop() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom c1 = atom(CARBON);
		Atom c2 = atom(CARBON);
		Atom a = atom(BasicType.A);
		seed.bondWith(c1);
		c1.bondWith(c2);
		c2.bondWith(seed);
		atoms.addAll(Arrays.asList(seed, a, c2, c1));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_5);
	}
	
	@Test
	public void failWhenIsolatedCarbonLine() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom c1 = atom(CARBON);
		Atom c2 = atom(CARBON);
		Atom c3 = atom(CARBON);
		seed.bondWith(c1);
		c2.bondWith(c3);
		atoms.addAll(Arrays.asList(seed, c2, c1, c3));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_4);
	}


	@Test
	public void failWhenCarbonStructure() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom c1 = atom(CARBON);
		Atom c2 = atom(CARBON);
		Atom c3 = atom(CARBON);
		seed.bondWith(c1);
		c1.bondWith(c2);
		c1.bondWith(c3);
		atoms.addAll(Arrays.asList(seed, c2, c1, c3));
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
		LineValidator validator = new LineValidator(CARBON, SEED_STATE);
		validator.setup(Collections.list(seed));
		return validator.evaluate(atoms, messages);
	}
}
