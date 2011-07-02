package uk.org.squirm3.engine.generator;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;

public interface AtomGenerator {

    Collection<Atom> generate(Configuration configuration) throws GeneratorException;

}
