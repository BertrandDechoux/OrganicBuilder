package uk.org.squirm3.ui.toolbar.simulation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;

/**
 * Stop the simulation ie the level. The button will be active if and only if
 * the simulation is running.
 */
public class StopSimulationButton extends Button {
	public StopSimulationButton(final ApplicationEngine applicationEngine) {
		this.setOnAction((ActionEvent e) -> {
			applicationEngine.pauseSimulation();
		});

		applicationEngine.addListener(() -> {
			Platform.runLater(() -> {
				StopSimulationButton.this.setDisable(!applicationEngine.simulationIsRunning());
			});
		} , ApplicationEngineEvent.SIMULATION_STATE);
	}
}
