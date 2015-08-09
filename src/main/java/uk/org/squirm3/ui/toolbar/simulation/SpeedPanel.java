package uk.org.squirm3.ui.toolbar.simulation;

import java.awt.Dimension;

import javax.swing.JPanel;
import org.springframework.context.MessageSource;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.springframework.Messages;

/**
 * Allows user to change the speed of the simulation.
 */
public class SpeedPanel extends JPanel implements Listener {
	private static final long serialVersionUID = 1L;

	private JFXPanel futurContainer;
	private Slider speedSelector;

	private final ApplicationEngine applicationEngine;

	public SpeedPanel(final ApplicationEngine applicationEngine, final MessageSource messageSource) {
		this.applicationEngine = applicationEngine;

		futurContainer = new JFXPanel();
		Platform.runLater(() -> {
			HBox mainBox = new HBox();
			Text label = new Text(Messages.localize("parameters.speed", messageSource));
			mainBox.getChildren().add(label);

			speedSelector = new Slider(1, 8, 1);
			speedSelector.setMajorTickUnit(1);
			speedSelector.setMinorTickCount(0);
			speedSelector.setSnapToTicks(true);
			speedSelector.setBlockIncrement(1);
			speedSelector.setShowTickMarks(true);
			speedSelector.setShowTickLabels(false);
			speedSelector.valueProperty().addListener(new SpeedSelectorListener());
			mainBox.getChildren().add(speedSelector);

			Scene scene = new Scene(mainBox, 250, 20);
			scene.setFill(Color.BLACK);
			futurContainer.setScene(scene);
			applicationEngine.addListener(this, ApplicationEngineEvent.SPEED);
		});
		add(futurContainer);
		setMinimumSize(new Dimension(250, 20));
		// setMaximumSize(getPreferredSize());
	}

	/**
	 * Convert non-linear mapping : selector to engine.
	 */
	private void updateEngineSpeed() {
		applicationEngine.setSimulationSpeed((short) Math.pow(9 - speedSelector.getValue(), 2));
	}

	/**
	 * Convert non-linear mapping : engine to selector.
	 */
	@Override
	public void propertyHasChanged() {
		Platform.runLater(() -> {
			speedSelector.setValue(9 - (int) Math.sqrt(applicationEngine.getSimulationSpeed()));
		});
	}

	/**
	 * When the user has picked a new value for the selector, update the engine.
	 */
	private final class SpeedSelectorListener implements ChangeListener<Number> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.
		 * ObservableValue, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			updateEngineSpeed();
		}

	}
}
