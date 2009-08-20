package uk.org.squirm3;

import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.ILevel;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.ui.GUI;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * ${my.copyright}
 */

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
    public static void main(String argv[]) {
        try {
            String userLanguage = null;
            String userRegion = null;
            for (String arg : argv) {
                String[] entry = arg.split("=");
                if (entry.length != 2) throw new IllegalArgumentException(arg);
                for (int i = 0; i < entry.length; i++) {
                    entry[i] = entry[i].trim();
                }
                if (entry[0].equals(USER_LANGUAGE)) userLanguage = entry[1];
                else if (entry[0].equals(USER_REGION)) userRegion = entry[1];
            }
            Locale userLocale = (userLanguage == null) ? null :
                    new Locale(userLanguage, userRegion);
            runApplication(null, userLocale);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * main logic for running the application
     */
    public static void runApplication(JRootPane rootPane, Locale locale) throws Exception {
        try {
            // load configuration
            CONFIGURATION.load(Resource.class.getResourceAsStream(CONFIGURATION_FILE_PATH));

            // load i18n info
            if (locale == null) locale = Locale.getDefault();
            INTERFACE_BUNDLE = ResourceBundle.getBundle(
                    getConfigurationProperty("i18n.interface"), locale);
            LEVELS_BUNDLE = ResourceBundle.getBundle(
                    getConfigurationProperty("i18n.levels"), locale);

            // create the user interface
            GUI.createGUI(new ApplicationEngine(), rootPane);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getConfigurationProperty(String key) {
        String value = CONFIGURATION.getProperty(key, null);
        if (value == null) throw new MissingResourceException("", "configuration", key);
        return value;
    }

    public static List<ILevel> getLevels() throws Exception {
        final int simulationWidth = Integer.parseInt(getConfigurationProperty("simulation.width"));
        final int simulationHeight = Integer.parseInt(getConfigurationProperty("simulation.height"));
        final int numberOfAtoms = Integer.parseInt(getConfigurationProperty("simulation.atom.number"));

        final Configuration configuration = new Configuration(numberOfAtoms, Level.TYPES, simulationWidth, simulationHeight);

        final int numberOfLevels = Integer.parseInt(getConfigurationProperty("levels.number"));
        List<ILevel> levels = new ArrayList<ILevel>(numberOfLevels);

        for (int i = 0; i < numberOfLevels; i++) {
            String className = getConfigurationProperty("levels." + i + ".class");
            String key = getConfigurationProperty("levels." + i + ".key");

            final String title = LEVELS_BUNDLE.getString(key + ".title");
            final String texte = LEVELS_BUNDLE.getString(key + ".challenge");
            final String hint = LEVELS_BUNDLE.getString(key + ".hint");
            final int numberOfErrors = Integer.parseInt(LEVELS_BUNDLE.getString(key + ".errors"));
            String[] errors = new String[numberOfErrors];
            for (int j = 1; j <= numberOfErrors; j++) {
                errors[j - 1] = LEVELS_BUNDLE.getString(key + ".error." + new Integer(j));
            }

            Class c = Class.forName(className);
            Constructor[] cs = c.getConstructors();
            Object[] os = new Object[]{title, texte, hint, errors, configuration};
            Level level = (Level) cs[0].newInstance(os);
            levels.add(level);
        }
        return Collections.unmodifiableList(levels);
    }

    public static ILogger getLogger() {
        return new NetLogger(Application.getConfigurationProperty("logger.url"));
    }

    public static String localize(String key) {
        return INTERFACE_BUNDLE.getString(key);
    }

}