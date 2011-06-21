package uk.org.squirm3.engine.generator;

public class BuilderException extends Exception {
    private static final long serialVersionUID = 1L;

    public BuilderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BuilderException(final String message) {
        super(message);
    }

}
