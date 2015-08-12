package uk.org.squirm3.ui.reaction.mode;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.ui.reaction.ReactionListPanel;

public class ListViewMode implements ReactionsListMode {
	private Button editButton, deleteButton, clearButton;
	private ListView<Reaction> reactionsList;
	private Pane buttonsPane;

	public ListViewMode(final ReactionListPanel reactionListPanel, final ReactionsListMode textMode) {
		buttonsPane = reactionListPanel.createButtonsPane();
		reactionsList = new ListView<>();

		EventHandler<ActionEvent> editReactionHandler = (ActionEvent e) -> {
			reactionListPanel.setCurrentModeTo(textMode);
		};
		editButton = reactionListPanel.createButton("reactions.edit", editReactionHandler);

		EventHandler<ActionEvent> deleteSelectedReactionsHandler = (ActionEvent e) -> {
			reactionListPanel.removeReactions( //
					new ArrayList<>(reactionsList.getSelectionModel().getSelectedItems()));
		};
		deleteButton = reactionListPanel.createButton("reactions.delete", deleteSelectedReactionsHandler);

		EventHandler<ActionEvent> cleanReactionsHandler = (ActionEvent e) -> {
			reactionListPanel.clearReactions();
		};
		clearButton = reactionListPanel.createButton("reactions.clear", cleanReactionsHandler);
		buttonsPane.getChildren().addAll(editButton, deleteButton, clearButton);

		reactionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		reactionsList//
				.getSelectionModel()//
				.selectedItemProperty()//
				.addListener((r) -> {
					updateDeleteButton();
				});
		reactionsList.setOnMouseClicked((e) -> {
			if (e.getClickCount() == 2) {
				editReactionHandler.handle(null);
			}
		});
	}

	@Override
	public Node getMenu() {
		return buttonsPane;
	}

	@Override
	public Node getReactionsList() {
		return reactionsList;
	}

	@Override
	public void reactionsHaveChanged(final Collection<Reaction> reactions) {
		Platform.runLater(() -> {
			reactionsList.setItems(FXCollections.observableList(Lists.newArrayList(reactions)));
			clearButton.setDisable(reactions.isEmpty());
			updateDeleteButton();
		});
	}

	private void updateDeleteButton() {
		Platform.runLater(() -> {
			deleteButton.setDisable(reactionsList.getSelectionModel().isEmpty());
		});
	}

}
