package uk.org.squirm3.springframework;

import java.util.Locale;

import org.springframework.context.MessageSource;

public abstract class Messages {

    public static String localize(final String key,
            final MessageSource messageSource) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}
