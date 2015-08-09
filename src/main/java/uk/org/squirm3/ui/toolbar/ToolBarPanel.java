package uk.org.squirm3.ui.toolbar;

import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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

public class ToolBarPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color BACKGROUND = Color.rgb(255, 255, 225);

	public ToolBarPanel(final Button runSimulationButton, final Button stopSimulationButton,
			final Button resetSimulationButton, final SpeedPane speedPanel, final Button firstLevelButton,
			final Button previousLevelButton, final LevelPicker levelPicker, final Button nextLevelButton,
			final Button lastLevelButton, final Button aboutButton) {

		JFXPanel futurContainer = new JFXPanel();
		add(futurContainer);
		Platform.runLater(() -> {
			HBox mainBox = new HBox(5);
			mainBox.setBackground(new Background(new BackgroundFill(BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
			
			Scene scene = new Scene(mainBox, 1000, 45);
			scene.setFill(BACKGROUND);
			futurContainer.setScene(scene);

			mainBox.setPadding(new Insets(5));
			addButtons(mainBox, stopSimulationButton, runSimulationButton, resetSimulationButton);

			addGlueSeparartor(mainBox);

			speedPanel.setBackground(new Background(new BackgroundFill(BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
			speedPanel.setPadding(new Insets(8, 0, 8, 0));
			mainBox.getChildren().add(speedPanel);

			addGlueSeparartor(mainBox);

			addButtons(mainBox, firstLevelButton, previousLevelButton);
			levelPicker.setMaxSize(150, 18);
			BorderPane levelPickerPane = new BorderPane();
			levelPickerPane.setCenter(levelPicker);
			
			mainBox.getChildren().add(levelPickerPane);
			addButtons(mainBox, nextLevelButton, lastLevelButton);

			addGlueSeparartor(mainBox);

			addButtons(mainBox, aboutButton);
		});
	}

	/**
	 * Add all provided buttons with additional configuration.
	 */
	private void addButtons(HBox mainBox, final Button... buttons) {
		for (final Button button : buttons) {
			mainBox.getChildren().add(createIconButton(button));
		}
	}

	/**
	 * Add a glue component that will grow if there is extra space.
	 */
	private void addGlueSeparartor(HBox mainBox) {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		mainBox.getChildren().add(spacer);
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
