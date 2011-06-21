package uk.org.squirm3.model.level;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class LevelMessages {

    private final String key;
    private final MessageSource messageSource;

    public LevelMessages(final String key, final MessageSource messageSource) {
        this.key = key;
        this.messageSource = messageSource;
    }

    public String getTitle() {
        return localize("title");
    }

    public String getChallenge() {
        return localize("challenge");
    }

    public String getHint() {
        return localize("hint");
    }

    public String getError(final int errorNumber) {
        return localize("error." + errorNumber);
    }

    public String localize(final String subKey) {
        return messageSource.getMessage(key + "." + subKey, null,
                Locale.getDefault());
    }

}
