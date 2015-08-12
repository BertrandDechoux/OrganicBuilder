package uk.org.squirm3.ui.toolbar.navigation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;

/**
 * Go to the next level.The button will be active if and only if the current
 * level has a next level.
 */
public class NextLevelButton extends Button {
	public NextLevelButton(final ApplicationEngine applicationEngine) {
		this.setOnAction((ActionEvent e) -> {
			applicationEngine.goToNextLevel();
		});

		applicationEngine.addListener(() -> {
			final boolean lastLevel = applicationEngine.getLevelManager().isCurrentLevelLastLevel();
			Platform.runLater(() -> {
				NextLevelButton.this.setDisable(lastLevel);
			});
		} , ApplicationEngineEvent.LEVEL);
	}
}
