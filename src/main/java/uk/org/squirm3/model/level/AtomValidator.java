package uk.org.squirm3.model.level;

import java.util.Collection;

import uk.org.squirm3.model.Atom;

public interface AtomValidator {

    void setup(Collection<? extends Atom> atoms);

    String evaluate(Collection<? extends Atom> atoms, LevelMessages messages);

}
