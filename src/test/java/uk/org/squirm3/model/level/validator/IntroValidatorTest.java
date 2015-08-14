package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.IntroValidator;

public class IntroValidatorTest extends ValidatorTest {
	@Test
	public void isAlwaysASuccess() {
		// given
		List<Atom> atoms = new ArrayList<>();
		// when
		String errorMessage = new IntroValidator().evaluate(atoms, messages);
		// then
		assertThat(errorMessage).isNull();
	}

}
