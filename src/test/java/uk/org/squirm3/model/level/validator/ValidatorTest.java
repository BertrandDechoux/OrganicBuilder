package uk.org.squirm3.model.level.validator;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.FixedPoint;
import uk.org.squirm3.model.level.LevelMessages;
import uk.org.squirm3.model.type.AtomType;

@RunWith(MockitoJUnitRunner.class)
public abstract class ValidatorTest {
	protected static final String ERROR_1 = "error 1";
	protected static final String ERROR_2 = "error 2";
	protected static final String ERROR_3 = "error 3";
	protected static final String ERROR_4 = "error 4";

	@Mock
	protected LevelMessages messages;

	@Before
	public void setup() {
		when(messages.getError(anyInt())).then(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return "error " + invocation.getArgumentAt(0, Integer.class);
			}
		});
	}

	protected Atom atom(AtomType type) {
		return new Atom(new FixedPoint(0, 0), type, 0);
	}}
