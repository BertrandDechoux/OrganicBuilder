package uk.org.squirm3.ui;

import javax.swing.JComponent;

import org.springframework.context.MessageSource;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
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
			final AtomsPanel collisionsPanel, final ToolBarPanel toolBarPanel) {

		Platform.runLater(() -> {
			SplitPane reactionsPane = createReactionsPane(reactionListPanel, reactionConstructorPanel);
			SplitPane rootComponent = buildRootComponent(collisionsPanel, currentLevelPanel, reactionsPane);
			final BorderPane contentPane = buildContentPane(toolBarPanel, rootComponent);

			showApplication(primaryStage, contentPane, Messages.localize("application.title", messageSource));
		});

	}

	/**
	 * Bottom-left component.
	 */
	private SplitPane createReactionsPane(final ReactionListPanel reactionListPanel,
			final ReactionConstructorPanel reactionConstructorPanel) {
		final SplitPane reactionsPane = new SplitPane();
		reactionsPane.setOrientation(Orientation.VERTICAL);
		reactionsPane.getItems().addAll(reactionConstructorPanel, reactionListPanel);
		return reactionsPane;
	}

	/**
	 * The main component (without the toolbar)
	 */
	private SplitPane buildRootComponent(final JComponent collisionsPanel, final CurrentLevelPanel currentLevelPanel,
			final SplitPane reactionsPane) {
		final SplitPane leftComponent = new SplitPane();
		leftComponent.setOrientation(Orientation.VERTICAL);
		leftComponent.getItems().addAll(currentLevelPanel, reactionsPane);

		final SplitPane rootComponent = new SplitPane();
		rootComponent.setOrientation(Orientation.HORIZONTAL);
		final SwingNode swingNode = new SwingNode();
		swingNode.setContent(collisionsPanel);
		rootComponent.getItems().addAll(leftComponent, swingNode);
		return rootComponent;
	}

	/**
	 * The whole graphical user interface.
	 */
	private BorderPane buildContentPane(final ToolBarPanel toolBarPanel, final SplitPane rootComponent) {
		final BorderPane contentPane = new BorderPane();
		contentPane.setTop(toolBarPanel);
		contentPane.setCenter(rootComponent);
		return contentPane;
	}

	private void showApplication(Stage primaryStage, BorderPane pane, String title) {
		primaryStage.setMaximized(true);
		primaryStage.setFullScreen(true);

		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});

		Scene scene = new Scene(pane);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
