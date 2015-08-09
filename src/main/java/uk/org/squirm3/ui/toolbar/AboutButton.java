package uk.org.squirm3.ui.toolbar;

import org.springframework.context.MessageSource;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.Utils;

/**
 * Display information about the application.
 */
public class AboutButton extends Button {
	private final MessageSource messageSource;

	public AboutButton(final MessageSource messageSource) {
		this.messageSource = messageSource;
		this.setOnAction((ActionEvent e) -> {
			Utils.modalAlert(AlertType.INFORMATION, //
					localize("about.title"), //
					localize("about.text"), //
					this.getScene().getWindow());
		});
	}

	private final String localize(final String key) {
		return Messages.localize(key, messageSource);
	}

}
