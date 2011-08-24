package uk.org.squirm3.model.level;

import java.util.Collection;

import uk.org.squirm3.engine.generator.GeneratorException;
import uk.org.squirm3.engine.generator.LevelConstructor;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;

public class ComposedLevel implements Level {

    private final LevelConstructor levelConstructor;
    private final LevelMessages messages;
    private final AtomValidator atomValidator;

    public ComposedLevel(final LevelConstructor levelConstructor,
            final LevelMessages messages, final AtomValidator atomValidator) {
        this.levelConstructor = levelConstructor;
        this.messages = messages;
        this.atomValidator = atomValidator;
    }

    @Override
    public String getTitle() {
        return messages.getTitle();
    }

    @Override
    public String getChallenge() {
        return messages.getChallenge();
    }

    @Override
    public String getHint() {
        return messages.getHint();
    }

    @Override
    public Configuration construct() {
        try {
            final Configuration configuration = levelConstructor.construct();
            atomValidator.setup(configuration.getAtoms());
            return configuration;
        } catch (final GeneratorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms) {
        return atomValidator.evaluate(atoms, messages);
    }

}
