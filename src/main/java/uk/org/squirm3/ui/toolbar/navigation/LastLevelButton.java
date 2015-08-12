package uk.org.squirm3.ui.toolbar.navigation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;

/**
 * Go to the last level. The button will be active if and only if the current
 * level is not the last level.
 */
public class LastLevelButton extends Button {
	public LastLevelButton(final ApplicationEngine applicationEngine) {
		this.setOnAction((ActionEvent e) -> {
			applicationEngine.goToLastLevel();
		});

		applicationEngine.addListener(() -> {
			final boolean lastLevel = applicationEngine.getLevelManager().isCurrentLevelLastLevel();
			Platform.runLater(() -> {
				LastLevelButton.this.setDisable(lastLevel);
			});
		} , ApplicationEngineEvent.LEVEL);
	}

}
