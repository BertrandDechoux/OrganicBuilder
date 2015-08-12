package uk.org.squirm3.ui.level;

import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
		private final ApplicationEngine applicationEngine;

		private EvaluateHandler(final ApplicationEngine applicationEngine) {
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
			Utils.modalAlert(AlertType.ERROR, //
					localize("level.error.title"), //
					localize("level.error") + result, //
					CurrentLevelPanel.this.getScene().getWindow());
		}

		private void showLevelClearedMessage() {
			Optional<ButtonType> result = Utils.modalAlert(AlertType.CONFIRMATION, //
					localize("level.success.title"), //
					localize("level.success"), //
					CurrentLevelPanel.this.getScene().getWindow());
			if (result != null && result.get() == ButtonType.OK){
				applicationEngine.goToNextLevel();
			}
		}

		private void showLastLevelClearedMessage() {
			Utils.modalAlert(AlertType.INFORMATION, //
					localize("level.success.title"), //
					localize("level.fullsuccess"), //
					CurrentLevelPanel.this.getScene().getWindow());
		}
	}

	private final class HintHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Utils.modalAlert(AlertType.INFORMATION, //
					Messages.localize("level.hint", messageSource), //
					currentLevel.getHint(), //
					CurrentLevelPanel.this.getScene().getWindow());
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
