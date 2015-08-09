package uk.org.squirm3.ui.level;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.level.Level;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.Utils;

public class CurrentLevelPanel extends BorderPane {
	private final MessageSource messageSource;

	private WebEngine webEngine;
	private Button hintButton, evaluateButton;

	private Level currentLevel;

	public CurrentLevelPanel(final ApplicationEngine applicationEngine, final MessageSource messageSource) {
		this.messageSource = messageSource;

		WebView browser = new WebView();
		webEngine = browser.getEngine();
		setCenter(browser);

		BorderPane buttonsPane = new BorderPane();
		buttonsPane.setPadding(new Insets(8));
		hintButton = createButton("level.hint", new HintHandler());
		buttonsPane.setLeft(hintButton);
		evaluateButton = createButton("level.evaluate", new EvaluateHandler(applicationEngine));
		buttonsPane.setRight(evaluateButton);
		setBottom(buttonsPane);

		bindWithApplicationEngine(applicationEngine, messageSource);
		setMinSize(150, 150);
		Utils.defaultBorder(this);
	}

	private void bindWithApplicationEngine(final ApplicationEngine applicationEngine,
			final MessageSource messageSource) {
		applicationEngine.addListener(new LevelListener(applicationEngine, messageSource),
				ApplicationEngineEvent.LEVEL);
	}

	private Button createButton(final String key, final EventHandler<ActionEvent> handler) {
		final Button button = new Button(localize(key));
		Utils.defaultSize(button);
		button.setOnAction(handler);
		return button;
	}

	private String localize(final String key) {
		return Messages.localize(key, messageSource);
	}

	private final class EvaluateHandler implements EventHandler<ActionEvent> {
		private final Object[] options;
		private final ApplicationEngine applicationEngine;

		private EvaluateHandler(final ApplicationEngine applicationEngine) {
			options = new Object[] { localize("level.yes"), localize("level.no") };
			this.applicationEngine = applicationEngine;
		}

		@Override
		public void handle(ActionEvent event) {
			final String result = currentLevel.evaluate(applicationEngine.getAtoms());
			if (result == null) {
				if (applicationEngine.getLevelManager().isCurrentLevelLastLevel()) {
					showLastLevelClearedMessage();
				} else {
					showLevelClearedMessage();
				}
			} else {
				showErrorMessage(result);
			}
		}

		private void showErrorMessage(final String result) {
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(//
						null, localize("level.error") + result, //
						localize("level.error.title"), //
						JOptionPane.ERROR_MESSAGE);
			});
		}

		private void showLevelClearedMessage() {
			SwingUtilities.invokeLater(() -> {
				final int n = JOptionPane.showOptionDialog(//
						null, //
						localize("level.success"), //
						localize("level.success.title"), //
						JOptionPane.YES_NO_OPTION, //
						JOptionPane.INFORMATION_MESSAGE, //
						null, options, options[0]);
				if (n == JOptionPane.YES_OPTION) {
					applicationEngine.goToNextLevel();
				}
			});
		}

		private void showLastLevelClearedMessage() {
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(//
						null, //
						localize("level.fullsuccess"), //
						localize("level.success.title"), //
						JOptionPane.INFORMATION_MESSAGE);
			});
		}
	}

	private final class HintHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			SwingUtilities.invokeLater(() -> {
				JOptionPane.showMessageDialog(//
						null, //
						currentLevel.getHint(), //
						Messages.localize("level.hint", messageSource), //
						JOptionPane.INFORMATION_MESSAGE);
			});
		}
	}

	private final class LevelListener implements Listener {
		private final ApplicationEngine applicationEngine;
		private final MessageSource messageSource;

		private LevelListener(final ApplicationEngine applicationEngine, final MessageSource messageSource) {
			this.applicationEngine = applicationEngine;
			this.messageSource = messageSource;
		}

		@Override
		public void propertyHasChanged() {
			currentLevel = applicationEngine.getLevelManager().getCurrentLevel();
			if (currentLevel == null) {
				String levelDescription = Messages.localize("level.description.none", messageSource);
				Platform.runLater(() -> {
					webEngine.loadContent(levelDescription);
					hintButton.setDisable(true);
					evaluateButton.setDisable(true);
				});
			} else {
				String levelDescription = "<b>" + currentLevel.getTitle() + "</b>" + currentLevel.getChallenge();
				Platform.runLater(() -> {
					webEngine.loadContent(levelDescription);
					hintButton.setDisable(!StringUtils.hasText(currentLevel.getHint()));
					evaluateButton.setDisable(false);
				});
			}
		}
	}
}
