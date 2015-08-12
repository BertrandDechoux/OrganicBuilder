package uk.org.squirm3.ui.toolbar.navigation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.MessageSource;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.model.level.Level;
import uk.org.squirm3.springframework.Messages;

public class LevelPicker extends ComboBox<String> {
	private boolean update = true;

	public LevelPicker(final ApplicationEngine applicationEngine, final MessageSource messageSource) {
		super(getLevelsLabels(applicationEngine));

		this.setOnAction((ActionEvent e) -> {
			if (!update) {
				applicationEngine.goToLevel(LevelPicker.this.getSelectionModel().getSelectedIndex());
			}
			update = false;
		});
		setTooltip(new Tooltip(Messages.localize("level.selected", messageSource)));
		applicationEngine.addListener(() -> {
			update = true;
			Platform.runLater(() -> {
				LevelPicker.this.getSelectionModel().select(applicationEngine.getLevelManager().getCurrentLevelIndex());
			});
		} , ApplicationEngineEvent.LEVEL);
	}

	private static ObservableList<String> getLevelsLabels(final ApplicationEngine applicationEngine) {
		final List<? extends Level> levelList = applicationEngine.getLevelManager().getLevels();
		final List<String> levelsLabels = new ArrayList<>();
		final Iterator<? extends Level> it = levelList.iterator();
		int i = 0;
		while (it.hasNext()) {
			String number = String.valueOf(i) + "  ";
			if (i < 10) {
				number += "  ";
			}
			levelsLabels.add(number + it.next().getTitle());
			i++;
		}
		return FXCollections.observableList(levelsLabels);
	}

}
