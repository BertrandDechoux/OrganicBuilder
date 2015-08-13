package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.level.validators.IntroValidator;

@RunWith(MockitoJUnitRunner.class)
public class IntroValidatorTest {
	@Mock
	private LevelMessages messages;

	@Test
	public void isAlwaysASuccess() {
		List<Atom> atoms = new ArrayList<>();
		String errorMessage = new IntroValidator().evaluate(atoms, messages);
		assertThat(errorMessage).isNull();
		verifyZeroInteractions(messages);
	}

}
