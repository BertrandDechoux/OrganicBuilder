package uk.org.squirm3.ui.toolbar.navigation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;

/**
 * Go to the first level. The button will be active if and only if the current
 * level is not the first level.
 */
public class FirstLevelButton extends Button {
	public FirstLevelButton(final ApplicationEngine applicationEngine) {
		this.setOnAction((ActionEvent e) -> {
			applicationEngine.goToFirstLevel();
		});

		applicationEngine.addListener(() -> {
			final boolean firstLevel = applicationEngine.getLevelManager().isCurrentLevelFirstLevel();
			Platform.runLater(() -> {
				FirstLevelButton.this.setDisable(firstLevel);
			});
		} , ApplicationEngineEvent.LEVEL);
	}

}
