package uk.org.squirm3.springframework;

import javax.swing.JFrame;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.org.squirm3.ui.UiScaling;
import uk.org.squirm3.ui.theme.UiThemes;

/**
 * Startup for standalone application using a spring xml application context for
 * configuration.
 * 
 * TODO user should be able to specify the name and location of the
 * configuration regardless of the type (xml, annotation)
 */
public class StandaloneSpringLauncher {
    public static final String DEFAULT_APPLICATION_CONTEXT = "application-context.xml";

    /**
     * Load the default xml application context provided in the classpath.
     * 
     * XXX If any bean need to be created, this is the responsibility of the
     * application context itself and not of this method.
     * 
     * @param args unused arguments from cli
     */
    public static void main(final String... args) {
        UiScaling.adaptToScreenResolution();
        UiThemes.installSavedTheme();
        JFrame.setDefaultLookAndFeelDecorated(true);
        new ClassPathXmlApplicationContext(DEFAULT_APPLICATION_CONTEXT);
    }

}
