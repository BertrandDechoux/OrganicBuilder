package uk.org.squirm3.ui.toolbar;

import java.awt.Component;
import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.springframework.context.MessageSource;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.springframework.Messages;

/**
 * Display information about the application.
 */
public class AboutButton extends Button {
	private final MessageSource messageSource;
	private final String siteUrl;

	public AboutButton(final String siteUrl, final MessageSource messageSource) {
		this.messageSource = messageSource;
		this.siteUrl = siteUrl;
		this.setOnAction((ActionEvent e) -> {
			if (openUrlOnPage()) {
				return;
			}
			showUrlWithMessage(null);
		});
	}

	/**
	 * Show a message with the url inside.
	 */
	private void showUrlWithMessage(final java.awt.event.ActionEvent event) {
		// TODO
		SwingUtilities.invokeLater(() -> {
			final Component component = (Component) (event.getSource() instanceof Component ? event.getSource() : null);
			JOptionPane.showMessageDialog(component, localize("about.text"), localize("about.title"),
					JOptionPane.QUESTION_MESSAGE);
		});
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
