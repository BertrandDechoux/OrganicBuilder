package uk.org.squirm3.model.level.validator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.level.validators.ConnectValidator;
import uk.org.squirm3.model.type.def.BasicType;

public class ConnectValidatorTest extends ValidatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Atom upperLeftCorner;
    private Atom bottomRightCorner;
    
    @Before
    public void setup() {
    	super.setup();
    	upperLeftCorner = atom(BasicType.A, 1);
    	bottomRightCorner = atom(BasicType.F, 1);
    }
    
	@Test
	public void failWhenUpperLeftCornerNotFound() {
		// expect
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Upper left corner not found.");
		
		// given
		List<Atom> atoms = new ArrayList<>();
		
		// when
		new ConnectValidator(BasicType.A, BasicType.F, 1).setup(atoms);
	}

	@Test
	public void failWheBottomRightCornerNotFound() {
		// expect
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Bottom right corner not found.");
		
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(upperLeftCorner);
		
		// when
		new ConnectValidator(BasicType.A, BasicType.F, 1).setup(atoms);
	}
	
	@Test
	public void failWhenCornersAreNotBonded() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(upperLeftCorner);
		atoms.add(bottomRightCorner);
		atoms.add(atom(BasicType.D));
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isEqualTo(ERROR_1);
	}

	@Test
	public void successWhenCornersAreBonded() {
		// given
		List<Atom> atoms = new ArrayList<>();
		atoms.add(upperLeftCorner);
		atoms.add(bottomRightCorner);
		Atom atom = atom(BasicType.D);
		atom.bondWith(upperLeftCorner);
		atom.bondWith(bottomRightCorner);
		atoms.add(atom);
		// when
		String errorMessage = evaluate(atoms);
		// then
		assertThat(errorMessage).isNull();
	}

	private String evaluate(List<Atom> atoms) {
		List<Atom> setupAtoms = new ArrayList<>();
		setupAtoms.add(upperLeftCorner);
		setupAtoms.add(bottomRightCorner);
		ConnectValidator validator = new ConnectValidator(BasicType.A, BasicType.F, 1);
		validator.setup(setupAtoms);
		return validator.evaluate(atoms, messages);
	}
}
