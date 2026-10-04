package uk.org.squirm3.ui.toolbar;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;

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
        final Component component = (Component) (event
                .getSource() instanceof Component ? event.getSource() : null);
        JOptionPane.showMessageDialog(component, createInfoText(),
                localize("about.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Message text with a clickable link to the project repository.
     */
    private JEditorPane createInfoText() {
        final JEditorPane text = new JEditorPane("text/html",
                "<html>" + localize("about.text") + " <a href=\"" + siteUrl
                        + "\">" + siteUrl + "</a></html>");
        text.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                Boolean.TRUE);
        text.setEditable(false);
        text.setOpaque(false);
        text.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                openUrl(e.getURL());
            }
        });
        return text;
    }

    /**
     * Open the url with the system browser. Environments without browser
     * support simply leave the link in the dialog.
     */
    private void openUrl(final URL url) {
        if (!Desktop.isDesktopSupported()
                || !Desktop.getDesktop()
                        .isSupported(java.awt.Desktop.Action.BROWSE)) {
            return;
        }
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (final IOException | URISyntaxException e) {
            // the link stays available in the dialog
        }
    }

    private final String localize(final String key) {
        return Messages.localize(key, messageSource);
    }

}
