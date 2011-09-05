package uk.org.squirm3.swing.action;

import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

import javax.swing.Action;

import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import static com.google.common.base.Preconditions.checkNotNull;

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

    private final Properties properties;
    private final MessageSource messageSource;
    private final ConversionService conversionService;
    private final Collection<? extends ActionProperty> actionProperties;

    /* package */ActionConfigurer(final Properties properties,
            final MessageSource messageSource,
            final ConversionService conversionService,
            final Collection<? extends ActionProperty> actionProperties) {
        this.properties = checkNotNull(properties);
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
    public Action configure(final Action action, final String identifier) {
        for (final ActionProperty property : actionProperties) {
            final String messageCode = property.getMessageCode(identifier);
            final String message = getMessage(messageCode);
            if (!StringUtils.hasText(message)) {
                continue;
            }

            final Object value = conversionService.convert(message,
                    property.getTargetType());
            if (value == null) {
                throw new IllegalStateException(
                        "Error during the conversion of " + message);
            }
            action.putValue(property.getSwingKey(), value);
        }
        return action;
    }

    /**
     * Search properties and if not found messages.
     */
    private String getMessage(final String messageCode) {
        final String messageFromProperty = properties.getProperty(messageCode);
        if (messageFromProperty != null) {
            return messageFromProperty;
        }

        return messageSource.getMessage(messageCode, null, null,
                Locale.getDefault());
    }

}
