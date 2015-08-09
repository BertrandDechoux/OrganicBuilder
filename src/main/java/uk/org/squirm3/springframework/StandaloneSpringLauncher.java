package uk.org.squirm3.springframework;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import uk.org.squirm3.config.ApplicationConfig;

/**
 * Startup for standalone application.
 */
public class StandaloneSpringLauncher {
    /**
     * @param args
     *            unused arguments from cli
     */
    public static void main(final String... args) {
        new AnnotationConfigApplicationContext(ApplicationConfig.class);
    }

}
