package uk.org.squirm3.ui.toolbar.simulation;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.engine.ApplicationEngine;

/**
 * Restart the simulation ie the level.
 */
public class ResetSimulationButton extends Button {
	public ResetSimulationButton(final ApplicationEngine applicationEngine) {
		this.setOnAction((ActionEvent e) -> {
			applicationEngine.restartLevel();
		});
	}
}
