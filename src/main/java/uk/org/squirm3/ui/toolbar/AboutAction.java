package uk.org.squirm3.ui.toolbar;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.springframework.context.MessageSource;

import uk.org.squirm3.springframework.Messages;

/**
 * Display information about the application.
 */
public class AboutAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private final MessageSource messageSource;
    private final String siteUrl;

    public AboutAction(final String siteUrl, final MessageSource messageSource) {
        this.messageSource = messageSource;
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
        if (openUrlOnPage()) {
            return;
        }
        showUrlWithMessage(event);
    }

    /**
     * Show a message with the url inside.
     */
    private void showUrlWithMessage(final ActionEvent event) {
        final Component component = (Component) (event.getSource() instanceof Component
                ? event.getSource()
                : null);
        JOptionPane.showMessageDialog(component, localize("about.text"),
                localize("about.title"), JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Try to open the url with a browser. If not supported or in case of error,
     * return false.
     */
    private final boolean openUrlOnPage() {
        if (Desktop.isDesktopSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(siteUrl));
                    return true;
                } catch (final Exception e) {
                    // on error show the message
                }
            }
        }
        return false;
    }

    private final String localize(final String key) {
        return Messages.localize(key, messageSource);
    }

}
