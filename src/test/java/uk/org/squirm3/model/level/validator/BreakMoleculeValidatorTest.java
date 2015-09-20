package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.BreakMoleculeValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class BreakMoleculeValidatorTest extends ValidatorTest {
	private static final BasicType ALUMINIUM = BasicType.A;
	private static final BasicType DUBNIUM = BasicType.D;
	private static final int MOLECULE_STATE = 1;
	private Atom aluminiumGate;
	private Atom dubniumGate;
	private Atom innerAlumnium;
	private Atom innerDubnium;
	private Atom aluminiumExtremity;
	private Atom dubniumExtremity;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		super.setup();
		aluminiumGate = atom(ALUMINIUM, MOLECULE_STATE);
		dubniumGate = atom(DUBNIUM, MOLECULE_STATE);
		aluminiumGate.bondWith(dubniumGate);
		innerAlumnium = atom(ALUMINIUM, MOLECULE_STATE);
		innerDubnium = atom(DUBNIUM, MOLECULE_STATE);
		aluminiumExtremity = atom(ALUMINIUM, MOLECULE_STATE);
		dubniumExtremity = atom(DUBNIUM, MOLECULE_STATE);
		innerAlumnium.bondWith(aluminiumGate);
		innerAlumnium.bondWith(aluminiumExtremity);
		innerDubnium.bondWith(dubniumGate);
		innerDubnium.bondWith(dubniumExtremity);
	}

	@Test
	public void failWhenMoleculeNotFound() {
		// expect
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No molecule found.");
		// given
		List<Atom> atoms = new ArrayList<>();
		// when
		new BreakMoleculeValidator(ALUMINIUM, DUBNIUM, MOLECULE_STATE).setup(atoms);
	}

	@Test
	public void failWhenGateNotBroken() {
		BreakMoleculeValidator validator = validator();
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.addAll(Arrays.asList(aluminiumGate, dubniumGate, innerAlumnium, innerDubnium, aluminiumExtremity, dubniumExtremity));
		// when
		String errorMessage = evaluate(validator, atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	@Test
	public void successWhenGateBroken() {
		BreakMoleculeValidator validator = validator();
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.addAll(Arrays.asList(aluminiumGate, dubniumGate, innerAlumnium, innerDubnium, aluminiumExtremity, dubniumExtremity));
		aluminiumGate.breakBondWith(dubniumGate);
		// when
		String errorMessage = evaluate(validator, atoms);
		// then
		assertThat(errorMessage).isNull();
	}
	
	@Test
	public void failWhenMoleculeIsModified() {
		BreakMoleculeValidator validator = validator();
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.addAll(Arrays.asList(aluminiumGate, dubniumGate, innerAlumnium, innerDubnium, aluminiumExtremity, dubniumExtremity));
		aluminiumGate.breakBondWith(dubniumGate);
		Atom extraCarbon = atom(BasicType.C);
		atoms.add(extraCarbon);
		innerAlumnium.bondWith(extraCarbon);
		// when
		String errorMessage = evaluate(validator, atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	private String evaluate(BreakMoleculeValidator validator, List<Atom> atoms) {
		return validator.evaluate(atoms, messages);
	}

	private BreakMoleculeValidator validator() {
		BreakMoleculeValidator validator = new BreakMoleculeValidator(ALUMINIUM, DUBNIUM, MOLECULE_STATE);
		validator.setup(Arrays.asList(aluminiumGate, dubniumGate, innerAlumnium, innerDubnium, aluminiumExtremity, dubniumExtremity));
		return validator;
	}
}
