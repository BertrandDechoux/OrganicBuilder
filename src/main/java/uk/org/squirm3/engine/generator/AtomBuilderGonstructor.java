package uk.org.squirm3.engine.generator;

import uk.org.squirm3.model.Configuration;

public class AtomBuilderGonstructor implements LevelConstructor {

    private final String levelDescription;
    private final AtomBuilder atomBuilder;

    public AtomBuilderGonstructor(final String levelDescription,
            final AtomBuilder atomBuilder) {
        this.levelDescription = levelDescription;
        this.atomBuilder = atomBuilder;
    }

    @Override
    public Configuration construct() throws GeneratorException {
        try {
            return atomBuilder.build(levelDescription);
        } catch (final Exception e) {
            // we want to be able to catch even unwanted exception (ie
            // RuntimeException)
            throw new GeneratorException("Unable to construct the level.", e);
        }
    }

}
