package uk.org.squirm3.ui;

import org.springframework.context.MessageSource;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.collider.AtomsPanel;
import uk.org.squirm3.ui.level.CurrentLevelPanel;
import uk.org.squirm3.ui.reaction.ReactionConstructorPanel;
import uk.org.squirm3.ui.reaction.ReactionListPanel;
import uk.org.squirm3.ui.toolbar.ToolBarPanel;

/**
 * Root of the graphical user interface. It setups the injected dependencies
 * within a layout.
 */
public class GUI {
	public GUI(Stage primaryStage, final MessageSource messageSource, final CurrentLevelPanel currentLevelPanel,
			final ReactionListPanel reactionListPanel, final ReactionConstructorPanel reactionConstructorPanel,
			final AtomsPanel atomsPanel, final ToolBarPanel toolBarPanel) {

		SplitPane toollessApp = buildToollessApp(currentLevelPanel, reactionConstructorPanel, reactionListPanel,
				atomsPanel);
		Parent app = addTools(toolBarPanel, toollessApp);
		showApplication(primaryStage, app, Messages.localize("application.title", messageSource));

	}

	/**
	 * The main component (without the toolbar)
	 */
	private SplitPane buildToollessApp(final CurrentLevelPanel currentLevelPanel,
			final ReactionConstructorPanel reactionConstructorPanel, final ReactionListPanel reactionListPanel,
			final AtomsPanel collisionsPanel) {
		BorderPane reactionsPane = new BorderPane();
		reactionsPane.setTop(reactionConstructorPanel);
		reactionsPane.setCenter(reactionListPanel);

		final SplitPane leftComponent = new SplitPane();
		leftComponent.setOrientation(Orientation.VERTICAL);
		leftComponent.getItems().addAll(currentLevelPanel, reactionsPane);

		final SplitPane toollessApp = new SplitPane();
		toollessApp.setOrientation(Orientation.HORIZONTAL);
		toollessApp.getItems().addAll(leftComponent, collisionsPanel);
		return toollessApp;
	}

	/**
	 * The whole graphical user interface.
	 */
	private Parent addTools(final ToolBarPanel toolBarPanel, final SplitPane rootComponent) {
		final BorderPane app = new BorderPane();
		app.setTop(toolBarPanel);
		app.setCenter(rootComponent);
		return app;
	}

	private void showApplication(Stage primaryStage, Parent app, String title) {
		primaryStage.setMaximized(true);
		primaryStage.setFullScreen(true);

		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});

		Scene scene = new Scene(app);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
