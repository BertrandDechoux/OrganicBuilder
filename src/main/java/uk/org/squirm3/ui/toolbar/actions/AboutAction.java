package uk.org.squirm3.ui.toolbar.actions;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.springframework.context.MessageSource;

/**
 * Display information about the application.
 */
public class AboutAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    private final String siteUrl;

    public AboutAction(final String siteUrl, final MessageSource messageSource,
            final ImageIcon aboutIcon) {
        super(messageSource, null, "about.tooltip", aboutIcon);
        this.siteUrl = siteUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (Desktop.isDesktopSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(siteUrl));
                    return;
                } catch (final Exception e) {
                    // on error show the message
                }
            }
        }
        final Component component = (Component) (event.getSource() instanceof Component
                ? event.getSource()
                : null);
        JOptionPane.showMessageDialog(component, localize("about.text"),
                localize("about.title"), JOptionPane.QUESTION_MESSAGE);
    }

}
