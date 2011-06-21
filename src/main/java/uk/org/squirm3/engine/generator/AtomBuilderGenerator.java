package uk.org.squirm3.engine.generator;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;

public class AtomBuilderGenerator implements AtomGenerator {

    private final String levelDescription;
    private final Configuration configuration;
    private final AtomBuilder atomBuilder;

    public AtomBuilderGenerator(final String levelDescription,
            final Configuration configuration, final AtomBuilder atomBuilder) {
        this.levelDescription = levelDescription;
        this.configuration = configuration;
        this.atomBuilder = atomBuilder;
    }

    @Override
    public Collection<Atom> generate() throws GeneratorException {
        try {
            return atomBuilder.build(levelDescription, configuration);
        } catch (final BuilderException e) {
            throw new GeneratorException(
                    "Unable to build the atoms for the level.", e);
        }
    }

}
