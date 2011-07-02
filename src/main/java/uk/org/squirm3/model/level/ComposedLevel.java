package uk.org.squirm3.model.level;

import java.util.Collection;
import java.util.List;

import uk.org.squirm3.engine.generator.AtomGenerator;
import uk.org.squirm3.engine.generator.GeneratorException;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;

import com.google.common.collect.Lists;

public class ComposedLevel implements Level {

    private final AtomGenerator atomGenerator;
    private final LevelMessages messages;
    private final AtomValidator atomValidator;

    public ComposedLevel(final AtomGenerator atomGenerator,
            final LevelMessages messages,
            final AtomValidator atomValidator) {
        this.atomGenerator = atomGenerator;
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
    public List<Atom> generateAtoms(final Configuration configuration) {
        try {
            final List<Atom> atoms = Lists.newArrayList(atomGenerator
                    .generate(configuration));
            atomValidator.setup(atoms);
            return atoms;
        } catch (final GeneratorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String evaluate(final Collection<? extends Atom> atoms) {
        return atomValidator.evaluate(atoms, messages);
    }

}
