package uk.org.squirm3.springframework;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Startup for standalone application using a spring xml application context for
 * configuration.
 * 
 * @enhancement user should be able to specify the name and location of the
 *              configuration regardless of the type (xml, annotation)
 */
public class StandaloneSpringLauncher {
    public static final String DEFAULT_APPLICATION_CONTEXT = "application-context.xml";

    /**
     * Load the default xml application context provided in the classpath.
     * 
     * @warn If any bean need to be created, this is the responsibility of the
     *       application context itself and not of this method.
     */
    public static void main(final String... args) {
        new ClassPathXmlApplicationContext(DEFAULT_APPLICATION_CONTEXT);
    }

}
