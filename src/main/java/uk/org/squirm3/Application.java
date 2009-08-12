package uk.org.squirm3;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.ui.GUI;
import uk.org.squirm3.ui.Resource;

import javax.swing.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

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
    private static final Properties PROPERTIES = new Properties();

    /**
     * run the application embedded inside a browser
     */
    public static void runAsApplet(JApplet applet) throws Exception {
        runApplication(applet, null);
    }

    /**
     * run the application as stand alone (Java Web Start or command line)
     */
    public static void main(String argv[]) throws Exception {
        runApplication(null, argv);
    }

    /**
     * main logic for running the application
     */
    private static void runApplication(final JApplet applet, String argv[]) throws Exception {
        try {
            setupConfiguration(applet, argv);
            setupLanguage();
            GUI.createGUI(new ApplicationEngine(), applet);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private static void setupConfiguration(final JApplet applet, String argv[]) throws IOException {
        // load the configuration properties
        Properties configurationProperties = new Properties();
        configurationProperties.load(Resource.class.getResourceAsStream(CONFIGURATION_FILE_PATH));

        if (applet != null) {
            // configuration from the applet overrides default configuration
            Iterator<?> it = configurationProperties.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = applet.getParameter(key);
                if (value != null) configurationProperties.setProperty(key, value);
            }
        } else {
            // configuration from the command line overrides default configuration
            for (int i = 0; i < argv.length; i++) {
                String[] entry = argv[i].split("=");
                if (entry.length == 2 && configurationProperties.containsKey(entry[0])) {
                    configurationProperties.setProperty(entry[0], entry[1]);
                }
            }
        }
        // save configuration
        loadSubProperties(PROPERTIES, configurationProperties, "configuration");
    }

    private static void setupLanguage() throws IOException {
        // is the language choice part of the configuration ?
        String key = "configuration.languages.choice";
        String chosenLanguage = getProperty(key);
        // else use default or ask the user
        if (chosenLanguage.equals(key) || chosenLanguage.trim().isEmpty()) {
            String[] languagesArray = getProperty("configuration.languages.available").split(" ");
            if (languagesArray.length == 1) chosenLanguage = languagesArray[0];
            else chosenLanguage = GUI.selectLanguage(languagesArray);
        }

        // find and load the messages used by the application
        String levelsTranslationFilePath = getProperty("configuration.translation.levels");
        String interfaceTranslationFilePath = getProperty("configuration.translation.interface");
        Properties levelsProperties = new Properties();
        Properties interfaceProperties = new Properties();
        // use files in the specified language
        // for more information, see http://www.loc.gov/standards/iso639-2/englangn.html
        try {
            levelsProperties.load(Resource.class.getResourceAsStream(levelsTranslationFilePath + "_" + chosenLanguage + ".properties"));
            interfaceProperties.load(Resource.class.getResourceAsStream(interfaceTranslationFilePath + "_" + chosenLanguage + ".properties"));
        } catch (Exception e) {// use default files
            levelsProperties.load(Resource.class.getResourceAsStream(levelsTranslationFilePath + ".properties"));
            interfaceProperties.load(Resource.class.getResourceAsStream(interfaceTranslationFilePath + ".properties"));
        } finally {
            loadSubProperties(PROPERTIES, levelsProperties, "levels");
            loadSubProperties(PROPERTIES, interfaceProperties, "interface");
        }
    }

    /**
     * get a configuration value or an internationalized message
     */
    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key, key);
    }

    @Deprecated
    public static String localize(String[] code) {
        StringBuilder key = new StringBuilder(code[0]);
        for (int i = 1; i < code.length; i++) {
            key.append(".");
            key.append(code[i]);
        }
        return getProperty(key.toString());
    }

    private static void loadSubProperties(Properties parent, Properties child, String discriminator) {
        discriminator += ".";
        Iterator<?> it = child.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = child.getProperty(key);
            parent.put(discriminator + key, value);
        }
    }
}
