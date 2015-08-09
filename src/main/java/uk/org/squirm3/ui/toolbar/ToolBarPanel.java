package uk.org.squirm3.ui.toolbar;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import uk.org.squirm3.ui.toolbar.navigation.LevelPicker;
import uk.org.squirm3.ui.toolbar.simulation.SpeedPane;

public class ToolBarPanel extends HBox {
	private static final Color BACKGROUND = Color.rgb(255, 255, 225);

	public ToolBarPanel(final Button runSimulationButton, final Button stopSimulationButton,
			final Button resetSimulationButton, final SpeedPane speedPanel, final Button firstLevelButton,
			final Button previousLevelButton, final LevelPicker levelPicker, final Button nextLevelButton,
			final Button lastLevelButton, final Button aboutButton) {
		super(5);
		
		setBackground(new Background(new BackgroundFill(BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
		setPadding(new Insets(5));
		
		addButtons(stopSimulationButton, runSimulationButton, resetSimulationButton);
		addHighPrioritySpacer();
		
		speedPanel.setBackground(new Background(new BackgroundFill(BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
		speedPanel.setPadding(new Insets(8, 0, 8, 0));
		getChildren().add(speedPanel);
		addHighPrioritySpacer();

		addButtons(firstLevelButton, previousLevelButton);
		levelPicker.setMaxSize(150, 18);
		BorderPane levelPickerPane = new BorderPane();
		levelPickerPane.setCenter(levelPicker);
		getChildren().add(levelPickerPane);
		addButtons(nextLevelButton, lastLevelButton);
		addHighPrioritySpacer();

		addButtons(aboutButton);
	}

	/**
	 * Add all provided buttons with additional configuration.
	 */
	private void addButtons(final Button... buttons) {
		for (final Button button : buttons) {
			getChildren().add(createIconButton(button));
		}
	}

	/**
	 * Add a region component that will grow if there is extra space.
	 */
	private void addHighPrioritySpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		getChildren().add(spacer);
	}

	/**
	 * @return a button configured as a standard icon
	 */
	public static Button createIconButton(final Button button) {
		button.setPadding(new Insets(-1, -1, -1, -1));
		button.setBackground(new Background(new BackgroundFill(BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
		return button;
	}

}
