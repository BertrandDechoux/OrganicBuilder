package uk.org.squirm3.ui.toolbar.navigation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;

/**
 * Go to previous level.The button will be active if and only if the current
 * level is has a previous level.
 */
public class PreviousLevelAction extends Button {
	public PreviousLevelAction(final ApplicationEngine applicationEngine) {
		this.setOnAction((ActionEvent e) -> {
			applicationEngine.goToPreviousLevel();
		});

		applicationEngine.addListener(() -> {
			final boolean firstLevel = applicationEngine.getLevelManager().isCurrentLevelFirstLevel();
			Platform.runLater(() -> {
				PreviousLevelAction.this.setDisable(firstLevel);
			});
		} , ApplicationEngineEvent.LEVEL);
	}
}
