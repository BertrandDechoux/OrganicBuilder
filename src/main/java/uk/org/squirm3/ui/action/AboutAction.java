package uk.org.squirm3.ui.action;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.springframework.context.MessageSource;

import uk.org.squirm3.ui.Messages;

public class AboutAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    
    private final String siteUrl;
    private final MessageSource messageSource;
    
    public AboutAction(final String siteUrl, final MessageSource messageSource, final ImageIcon aboutIcon) {
        this.siteUrl = siteUrl;
        this.messageSource = messageSource;
        putValue(Action.SHORT_DESCRIPTION, Messages.localize("about.tooltip", messageSource));
        putValue(Action.SMALL_ICON, aboutIcon);
    }
    

    @Override
    public void actionPerformed(ActionEvent event) {
        if (Desktop.isDesktopSupported()) {
            final Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(siteUrl));
                    return;
                } catch (IOException e) {
                    // on error show the message
                } catch (URISyntaxException e) {
                    // on error show the message
                }
            }
        }
        final Component component = (Component) ((event.getSource() instanceof Component)
                ? event.getSource()
                : null);
        JOptionPane.showMessageDialog(component,
                Messages.localize("about.text",messageSource),
                Messages.localize("about.title",messageSource),
                JOptionPane.QUESTION_MESSAGE);
    }
    
}

