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
import uk.org.squirm3.model.level.validators.MatchTemplateValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class BreakMoleculeValidatorTest extends ValidatorTest {
	private static final int TEMPLATE_STATE = 1;
	private Atom aTemplate;
	private Atom bTemplate;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		super.setup();
		aTemplate = atom(BasicType.A, 1);
		bTemplate = atom(BasicType.B, 1);
		aTemplate.bondWith(bTemplate);
	}

	@Test
	public void failWhenTemplateNotFound() {
		// expect
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No template found.");
		// given
		List<Atom> atoms = new ArrayList<>();
		// when
		new MatchTemplateValidator(TEMPLATE_STATE).setup(atoms);
	}

	@Test
	public void failWhenOnlyTemplate() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.addAll(Arrays.asList(aTemplate, bTemplate));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	@Test
	public void failWhenTemplateIsAugmentedPartially() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a = atom(BasicType.A);
		a.bondWith(aTemplate);
		atoms.addAll(Arrays.asList(aTemplate, bTemplate, a));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	@Test
	public void successWhenTemplateIsAugmented() {
		// given
		List<Atom> atoms = new ArrayList<>();
		Atom a = atom(BasicType.A);
		a.bondWith(aTemplate);
		Atom b = atom(BasicType.B);
		b.bondWith(bTemplate);
		atoms.addAll(Arrays.asList(aTemplate, bTemplate, a, b));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	private String evaluate(List<Atom> atoms) {
		MatchTemplateValidator validator = new MatchTemplateValidator(TEMPLATE_STATE);
		validator.setup(Arrays.asList(aTemplate, bTemplate));
		return validator.evaluate(atoms, messages);
	}
}
