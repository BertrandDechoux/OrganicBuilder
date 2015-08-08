package uk.org.squirm3.config.gui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Locale;

import javax.swing.Action;

import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Uses properties and i18n messages for configuring the properties of
 * {@link Action} at runtime, performing the required conversion logic through a
 * {@link ConversionService}.
 *
 * Messages can be empty but conversion failure are not expected ie
 * {@link RuntimeException} will be thrown.
 *
 */
public class ActionConfigurer {

    private final Environment environment;
    private final MessageSource messageSource;
    private final ConversionService conversionService;
    private final Collection<? extends ActionProperty> actionProperties;

    /* package */ActionConfigurer(final Environment environment, final MessageSource messageSource, final ConversionService conversionService,
            final Collection<? extends ActionProperty> actionProperties) {
        this.environment = checkNotNull(environment);
        this.messageSource = checkNotNull(messageSource);
        this.conversionService = checkNotNull(conversionService);
        this.actionProperties = checkNotNull(actionProperties);
    }

    /**
     * @param action
     *            the action that should be configured
     * @param identifier
     *            what characterize the messages for the provided action with
     *            regards to the others
     */
    public <T extends Action> T configure(final T action, final String identifier) {
        for (final ActionProperty property : this.actionProperties) {
            final String messageCode = property.getMessageCode(identifier);
            final String message = this.getMessage(messageCode);
            if (!StringUtils.hasText(message)) {
                continue;
            }

            final Object value = this.conversionService.convert(message, property.getTargetType());
            if (value == null) {
                throw new IllegalStateException("Error during the conversion of " + message);
            }
            action.putValue(property.getSwingKey(), value);
        }
        return action;
    }

    /**
     * Search properties and if not found messages.
     */
    private String getMessage(final String messageCode) {
        final String messageFromProperty = this.environment.getProperty(messageCode);
        if (messageFromProperty != null) {
            return messageFromProperty;
        }

        return this.messageSource.getMessage(messageCode, null, null, Locale.getDefault());
    }

}
