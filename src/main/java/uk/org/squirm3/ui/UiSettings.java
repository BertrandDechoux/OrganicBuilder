package uk.org.squirm3.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;

import org.springframework.context.MessageSource;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.theme.EvoloomLaf;

/**
 * User interface settings. The persisted theme and locale are installed
 * before any component is created; the Options menu allows switching
 * them and stores the choices via {@link Preferences}.
 */
public final class UiSettings {

    private static final String THEME_KEY = "theme";
    private static final String LANGUAGE_KEY = "language";
    // Fixed node path so stored choices survive class moves
    private static final Preferences PREFERENCES = Preferences.userRoot()
            .node("/uk/org/squirm3/ui/theme");

    // Hidden themes stay registered for later use; they apply when their
    // id is selected in the preferences.
    private static final List<Theme> THEMES = Arrays.asList(
            new Theme("intellij", "menu.theme.intellij", true,
                    FlatIntelliJLaf::new),
            new Theme("light", "menu.theme.light", false, FlatLightLaf::new),
            new Theme("dark", "menu.theme.dark", false, FlatDarkLaf::new),
            new Theme("darcula", "menu.theme.darcula", true,
                    FlatDarculaLaf::new),
            new Theme("evoloom", "menu.theme.evoloom", true, EvoloomLaf::new));

    // The default locale is kept as the "system" choice; null means the
    // bundled English resources.
    private static final List<Language> LANGUAGES = Arrays.asList(
            new Language(null, "menu.language.system"),
            new Language(Locale.ENGLISH, "menu.language.en"),
            new Language(Locale.FRANCE, "menu.language.fr"));

    private static final Theme DEFAULT_THEME = THEMES.get(0);
    private static final Language DEFAULT_LANGUAGE = LANGUAGES.get(0);

    // Captured before installSavedSettings may override the default locale
    private static final Locale SYSTEM_LOCALE = Locale.getDefault();

    private UiSettings() {
    }

    /**
     * Install the persisted theme and locale. Must run before the AWT
     * toolkit creates any component, i.e. before the application context
     * is loaded.
     */
    public static void installSavedSettings() {
        FlatLaf.setup(savedTheme().createLaf());
        final Locale locale = savedLanguage().locale;
        if (locale != null) {
            Locale.setDefault(locale);
        }
    }

    /**
     * The application menu bar: Options holds theme and language, Help
     * the about action.
     */
    public static JMenuBar createMenuBar(final MessageSource messageSource,
            final Action aboutAction) {
        final JMenu themesMenu = new JMenu(
                Messages.localize("menu.theme", messageSource));
        final ButtonGroup themeGroup = new ButtonGroup();
        final Theme currentTheme = savedTheme();
        for (final Theme theme : THEMES) {
            if (!theme.menuVisible) {
                continue;
            }
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(
                    Messages.localize(theme.labelKey, messageSource));
            item.setSelected(theme == currentTheme);
            item.addActionListener(event -> applyTheme(theme));
            themeGroup.add(item);
            themesMenu.add(item);
        }

        final JMenu languagesMenu = new JMenu(
                Messages.localize("menu.language", messageSource));
        final ButtonGroup languageGroup = new ButtonGroup();
        final Language currentLanguage = savedLanguage();
        for (final Language language : LANGUAGES) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(
                    Messages.localize(language.labelKey, messageSource));
            item.setSelected(language == currentLanguage);
            item.addActionListener(
                    event -> applyLanguage(language, messageSource));
            languageGroup.add(item);
            languagesMenu.add(item);
        }

        final JMenu optionsMenu = new JMenu(
                Messages.localize("menu.options", messageSource));
        optionsMenu.add(themesMenu);
        optionsMenu.add(languagesMenu);

        final JMenu helpMenu = new JMenu(
                Messages.localize("menu.help", messageSource));
        final JMenuItem infoItem = new JMenuItem(aboutAction);
        infoItem.setText(Messages.localize("menu.help.info", messageSource));
        helpMenu.add(infoItem);

        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private static void applyTheme(final Theme theme) {
        FlatLaf.setup(theme.createLaf());
        FlatLaf.updateUI();
        PREFERENCES.put(THEME_KEY, theme.id);
    }

    /**
     * Labels resolve through {@link Locale#getDefault()} when components
     * are created, so the locale change applies fully after a restart.
     */
    private static void applyLanguage(final Language language,
            final MessageSource messageSource) {
        if (language.locale == null) {
            PREFERENCES.remove(LANGUAGE_KEY);
            Locale.setDefault(SYSTEM_LOCALE);
        } else {
            PREFERENCES.put(LANGUAGE_KEY, language.locale.toLanguageTag());
            Locale.setDefault(language.locale);
        }
        JOptionPane.showMessageDialog(null, Messages.localize(
                "menu.language.restart", messageSource));
    }

    private static Theme savedTheme() {
        final String id = PREFERENCES.get(THEME_KEY, DEFAULT_THEME.id);
        for (final Theme theme : THEMES) {
            if (theme.id.equals(id)) {
                return theme;
            }
        }
        return DEFAULT_THEME;
    }

    private static Language savedLanguage() {
        final String tag = PREFERENCES.get(LANGUAGE_KEY, null);
        for (final Language language : LANGUAGES) {
            if (language.locale != null
                    && language.locale.toLanguageTag().equals(tag)) {
                return language;
            }
        }
        return DEFAULT_LANGUAGE;
    }

    /**
     * A named look-and-feel; {@code menuVisible} themes appear in the
     * Options ▸ Theme menu.
     */
    private static final class Theme {
        private final String id;
        private final String labelKey;
        private final boolean menuVisible;
        private final Supplier<LookAndFeel> lafFactory;

        private Theme(final String id, final String labelKey,
                final boolean menuVisible,
                final Supplier<LookAndFeel> lafFactory) {
            this.id = id;
            this.labelKey = labelKey;
            this.menuVisible = menuVisible;
            this.lafFactory = lafFactory;
        }

        private LookAndFeel createLaf() {
            return lafFactory.get();
        }
    }

    /**
     * A selectable locale; a {@code null} locale means following the
     * operating system default.
     */
    private static final class Language {
        private final Locale locale;
        private final String labelKey;

        private Language(final Locale locale, final String labelKey) {
            this.locale = locale;
            this.labelKey = labelKey;
        }
    }
}
