package uk.org.squirm3.ui.toolbar.simulation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;

/**
 * Run the simulation ie the level. The button will be active if and only if the
 * simulation has been stopped.
 */
public class RunSimulationButton extends Button {
    public RunSimulationButton(final ApplicationEngine applicationEngine) {
		this.setOnAction((ActionEvent e) -> {
			applicationEngine.runSimulation();
		});

		applicationEngine.addListener(() -> {
			Platform.runLater(() -> {
				RunSimulationButton.this.setDisable(applicationEngine.simulationIsRunning());
			});
		} , ApplicationEngineEvent.SIMULATION_STATE);
    }
}
