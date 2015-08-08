package uk.org.squirm3.engine.generator;

import uk.org.squirm3.model.Configuration;

public interface LevelConstructor {

    Configuration construct() throws GeneratorException;

}
