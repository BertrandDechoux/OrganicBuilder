package uk.org.squirm3.model.level;

import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ResourceLoader;

import uk.org.squirm3.engine.generator.AtomBuilder;
import uk.org.squirm3.engine.generator.AtomBuilderGenerator;
import uk.org.squirm3.engine.generator.AtomGenerator;

public class ComposedLevelFactory implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private final ConversionService conversionService;
    private final MessageSource messageSource;

    private final AtomBuilder atomBuilder;

    private ComposedLevelFactory(final ConversionService conversionService,
            final MessageSource messageSource, final AtomBuilder atomBuilder) {
        this.conversionService = conversionService;
        this.messageSource = messageSource;
        this.atomBuilder = atomBuilder;
    }

    public Level create(final String key, final AtomValidator atomValidator) {
        return create(key, key + ".map", atomValidator);
    }

    public Level createRandom(final String key,
            final AtomValidator atomValidator) {
        return create(key, "random.map", atomValidator);
    }

    private Level create(final String key, final String map,
            final AtomValidator atomValidator) {
        final String levelDescription = conversionService.convert(
                resourceLoader.getResource("classpath:levels/" + map),
                String.class);
        final AtomGenerator atomGenerator = new AtomBuilderGenerator(
                levelDescription, atomBuilder);
        final LevelMessages messages = new LevelMessages(key, messageSource);
        return new ComposedLevel(atomGenerator, messages, atomValidator);
    }

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
