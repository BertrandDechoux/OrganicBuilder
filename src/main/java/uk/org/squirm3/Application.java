package uk.org.squirm3;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.ILevel;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.ui.GUI;

public final class Application {

    /**
     * This is an utility class. Do no instantiate.
     */
    private Application() {
    }

    public static final String USER_LANGUAGE = "user.language";
    public static final String USER_REGION = "user.region";

    private static final String CONFIGURATION_FILE_PATH = "/configuration.properties";
    private static final Properties CONFIGURATION = new Properties();
    private static ResourceBundle INTERFACE_BUNDLE, LEVELS_BUNDLE;

    /**
     * run the application as stand alone (Java Web Start or command line)
     */
    public static void main(final String argv[]) {
        try {
            String userLanguage = null;
            String userRegion = null;
            for (final String arg : argv) {
                final String[] entry = arg.split("=");
                if (entry.length != 2) {
                    throw new IllegalArgumentException(arg);
                }
                for (int i = 0; i < entry.length; i++) {
                    entry[i] = entry[i].trim();
                }
                if (entry[0].equals(USER_LANGUAGE)) {
                    userLanguage = entry[1];
                } else if (entry[0].equals(USER_REGION)) {
                    userRegion = entry[1];
                }
            }
            final Locale userLocale = userLanguage == null ? null : new Locale(
                    userLanguage, userRegion);
            runApplication(userLocale);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * main logic for running the application
     */
    public static void runApplication(final Locale locale)
            throws Exception {
        
        try {
            // load configuration
            CONFIGURATION.load(Resource.class
                    .getResourceAsStream(CONFIGURATION_FILE_PATH));

            // load i18n info
            Locale localLocale = locale;
            if (localLocale == null) {
                localLocale = Locale.getDefault();
            }
            INTERFACE_BUNDLE = ResourceBundle.getBundle(
                    getConfigurationProperty("i18n.interface"), localLocale);
            LEVELS_BUNDLE = ResourceBundle.getBundle(
                    getConfigurationProperty("i18n.levels"), localLocale);

            // create the user interface
            GUI.createGUI(new ApplicationEngine());
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    private static String getConfigurationProperty(final String key) {
        final String value = CONFIGURATION.getProperty(key, null);
        if (value == null) {
            throw new MissingResourceException("", "configuration", key);
        }
        return value;
    }

    public static List<ILevel> getLevels() throws Exception {
        final int simulationWidth = Integer
                .parseInt(getConfigurationProperty("simulation.width"));
        final int simulationHeight = Integer
                .parseInt(getConfigurationProperty("simulation.height"));
        final int numberOfAtoms = Integer
                .parseInt(getConfigurationProperty("simulation.atom.number"));

        final Configuration configuration = new Configuration(numberOfAtoms,
                Level.TYPES, simulationWidth, simulationHeight);

        final int numberOfLevels = Integer
                .parseInt(getConfigurationProperty("levels.number"));
        final List<ILevel> levels = new ArrayList<ILevel>(numberOfLevels);

        for (int i = 0; i < numberOfLevels; i++) {
            final String className = getConfigurationProperty("levels." + i
                    + ".class");
            final String key = getConfigurationProperty("levels." + i + ".key");

            final String title = LEVELS_BUNDLE.getString(key + ".title");
            final String texte = LEVELS_BUNDLE.getString(key + ".challenge");
            final String hint = LEVELS_BUNDLE.getString(key + ".hint");
            final int numberOfErrors = Integer.parseInt(LEVELS_BUNDLE
                    .getString(key + ".errors"));
            final String[] errors = new String[numberOfErrors];
            for (int j = 1; j <= numberOfErrors; j++) {
                errors[j - 1] = LEVELS_BUNDLE.getString(key + ".error."
                        + new Integer(j));
            }

            final Class c = Class.forName(className);
            final Constructor[] cs = c.getConstructors();
            final Object[] os = new Object[]{title, texte, hint, errors,
                    configuration};
            final Level level = (Level) cs[0].newInstance(os);
            levels.add(level);
        }
        return Collections.unmodifiableList(levels);
    }

    public static String localize(final String key) {
        return INTERFACE_BUNDLE.getString(key);
    }

}
