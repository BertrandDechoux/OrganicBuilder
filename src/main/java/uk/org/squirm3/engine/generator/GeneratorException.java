package uk.org.squirm3.engine.generator;

public class GeneratorException extends Exception {
    private static final long serialVersionUID = 1L;

    public GeneratorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GeneratorException(final String cause) {
        super(cause);
    }

}
