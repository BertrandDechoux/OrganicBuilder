package uk.org.squirm3.swing.action;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

/**
 * Ensures that default behavior is to use {@link ActionProperties}.
 */
public class ActionConfigurerFactory {

    /**
     * By default, {@link ActionConfigurer} uses {@link ActionProperties}.
     */
    public static ActionConfigurer createDefaultConfigurer(
            final Properties properties, final MessageSource messageSource,
            final ConversionService conversionService) {
        return new ActionConfigurer(properties, messageSource,
                conversionService, Arrays.asList(ActionProperties.values()));

    }

    /**
     * A custom list of {@link ActionProperty} can be provided.
     */
    public static ActionConfigurer createCustomConfigurer(
            final Properties properties, final MessageSource messageSource,
            final ConversionService conversionService,
            final Collection<? extends ActionProperty> actionProperties) {
        return new ActionConfigurer(properties, messageSource,
                conversionService, actionProperties);

    }

}
