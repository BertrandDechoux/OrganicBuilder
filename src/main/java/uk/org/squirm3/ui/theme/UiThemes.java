package uk.org.squirm3.ui.theme;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;

import org.springframework.context.MessageSource;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import uk.org.squirm3.springframework.Messages;

/**
 * Theme (look-and-feel) management. The persisted selection is installed
 * before any component is created; the Options menu allows switching at
 * runtime and stores the choice via {@link Preferences}.
 */
public final class UiThemes {

    private static final String THEME_KEY = "theme";
    private static final Preferences PREFERENCES = Preferences
            .userNodeForPackage(UiThemes.class);

    // Hidden themes stay registered for later use; they apply when their id
    // is selected in the preferences.
    private static final List<Theme> THEMES = Arrays.asList(
            new Theme("intellij", "menu.theme.intellij", true,
                    FlatIntelliJLaf::new),
            new Theme("light", "menu.theme.light", false, FlatLightLaf::new),
            new Theme("dark", "menu.theme.dark", false, FlatDarkLaf::new),
            new Theme("darcula", "menu.theme.darcula", false,
                    FlatDarculaLaf::new),
            new Theme("evoloom", "menu.theme.evoloom", true, EvoloomLaf::new));

    private static final Theme DEFAULT_THEME = THEMES.get(0);

    private UiThemes() {
    }

    /**
     * Install the persisted theme. Must run before the AWT toolkit creates
     * any component, i.e. before the application context is loaded.
     */
    public static void installSavedTheme() {
        FlatLaf.setup(savedTheme().createLaf());
    }

    /**
     * The application menu bar: Options holds the theme choice, Help the
     * about action.
     */
    public static JMenuBar createMenuBar(final MessageSource messageSource,
            final Action aboutAction) {
        final JMenu themesMenu = new JMenu(
                Messages.localize("menu.theme", messageSource));
        final ButtonGroup group = new ButtonGroup();
        final Theme current = savedTheme();
        for (final Theme theme : THEMES) {
            if (!theme.menuVisible) {
                continue;
            }
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(
                    Messages.localize(theme.labelKey, messageSource));
            item.setSelected(theme == current);
            item.addActionListener(event -> applyTheme(theme));
            group.add(item);
            themesMenu.add(item);
        }
        final JMenu optionsMenu = new JMenu(
                Messages.localize("menu.options", messageSource));
        optionsMenu.add(themesMenu);

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

    private static Theme savedTheme() {
        final String id = PREFERENCES.get(THEME_KEY, DEFAULT_THEME.id);
        for (final Theme theme : THEMES) {
            if (theme.id.equals(id)) {
                return theme;
            }
        }
        return DEFAULT_THEME;
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
}
