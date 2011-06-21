package uk.org.squirm3.engine.generator;

import java.util.Collection;

import uk.org.squirm3.model.Atom;

public interface AtomGenerator {

    Collection<Atom> generate() throws GeneratorException;

}
