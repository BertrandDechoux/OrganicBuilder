package uk.org.squirm3.ui.reaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.MessageSource;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.model.type.ReactionType;
import uk.org.squirm3.model.type.Types;
import uk.org.squirm3.springframework.Messages;

public class ReactionConstructorPanel extends VBox {
    private CheckBox bondedBefore, bondedAfter;
    private ComboBox<ReactionType> aType, bType;
    private ComboBox<?> aState, bState, futureAState, futureBState;
    private Label futureAType, futureBType;
    private Button addReaction;

    public ReactionConstructorPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final Image addIcon) {
    	super(5);
			
		getChildren().add(new Label(Messages.localize("reactions.editor", messageSource)));

		setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		final EventHandler<ActionEvent> updateReactionListener = (ActionEvent event) -> {
			Platform.runLater(() -> {
				futureAType.setText(toStringIdentifier(aType.getSelectionModel().getSelectedItem()));
				futureBType.setText(toStringIdentifier(bType.getSelectionModel().getSelectedItem()));
				addReaction.setText(createReactionFromEditor().toString());
			});
		};
		getChildren().add(createReactionForm(updateReactionListener));
		getChildren().add(createAddReactionButton(applicationEngine, messageSource, addIcon));
		updateReactionListener.handle(null);
		
		// XXX fix that
		setMinSize(100, 100);
		setPrefSize(100, 100);
    }

    private Pane createReactionForm(final EventHandler<ActionEvent> updateReactionHandler) {
    	FlowPane reactionForm = new FlowPane(Orientation.HORIZONTAL);
    	reactionForm.setColumnHalignment(HPos.CENTER);
        aType = createTypeComboBox(updateReactionHandler, reactionForm);
        aState = createStateComboBox(updateReactionHandler, reactionForm);
        bondedBefore = createBondedCheckBox(updateReactionHandler,
                reactionForm);
        bType = createTypeComboBox(updateReactionHandler, reactionForm);
        bState = createStateComboBox(updateReactionHandler, reactionForm);
        createLabel(" => ", reactionForm);
        futureAType = createLabel("", reactionForm);
        futureAState = createStateComboBox(updateReactionHandler, reactionForm);
        bondedAfter = createBondedCheckBox(updateReactionHandler, reactionForm);
        futureBType = createLabel("", reactionForm);
        futureBState = createStateComboBox(updateReactionHandler, reactionForm);
        return reactionForm;
    }

    private Button createAddReactionButton(
            final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final Image addIcon) {
        addReaction = new Button();
        addReaction.setGraphic(new ImageView(addIcon));
        addReaction.setPadding(new Insets(5, 10, 5, 10));
        addReaction.setTooltip(new Tooltip(Messages.localize("reactions.add.tooltip",
                messageSource)));
		addReaction.setOnAction((ActionEvent e) -> {
			applicationEngine.addReactions(Collections.singleton(createReactionFromEditor()));
		});
        return addReaction;
    }

	private Reaction createReactionFromEditor() {
		return new Reaction(aType.getSelectionModel().getSelectedItem(), //
				aState.getSelectionModel().getSelectedIndex(), //
				bondedBefore.isSelected(), //
				(ReactionType) bType.getSelectionModel().getSelectedItem(), //
				bState.getSelectionModel().getSelectedIndex(), //
				futureAState.getSelectionModel().getSelectedIndex(), //
				bondedAfter.isSelected(), //
				futureBState.getSelectionModel().getSelectedIndex());
	}

    private ComboBox<ReactionType> createTypeComboBox(
    		final EventHandler<ActionEvent> handler, final Pane parent) {
        List<ReactionType> items = new ArrayList<>(Types.getReactionTypes().size());
        for (final ReactionType reactionType : Types.getReactionTypes()) {
        	items.add(reactionType);
        }
        final ComboBox<ReactionType> comboBox = new ComboBox<>(FXCollections.observableList(items));
        comboBox.getSelectionModel().selectFirst();
        comboBox.setCellFactory((ListView<ReactionType> reactionTypes) -> {
        	return new ListCell<ReactionType>() {
                private final Label label = new Label();
                
                @Override protected void updateItem(ReactionType item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        label.setText(toStringIdentifier(item));
                        setGraphic(label);
                    }
               }
          };
        });
        comboBox.setOnAction(handler);
        parent.getChildren().add(comboBox);
        return comboBox;
    }

    private ComboBox<String> createStateComboBox(
            final EventHandler<ActionEvent> handler, final Pane parent) {
        List<String> items = new ArrayList<>(Configuration.MAX_NUMBER_OF_STATUS);
        for (int i = 0; i < Configuration.MAX_NUMBER_OF_STATUS; i++) {
        	items.add(String.valueOf(i));
        }
        final ComboBox<String> comboBox = new ComboBox<String>(FXCollections.observableList(items));
        comboBox.getSelectionModel().selectFirst();
        comboBox.setOnAction(handler);
        parent.getChildren().add(comboBox);
        return comboBox;
    }

    private CheckBox createBondedCheckBox(final EventHandler<ActionEvent> handler,
            final Pane parent) {
        final CheckBox checkBox = new CheckBox();
        checkBox.setOnAction(handler);
        parent.getChildren().add(checkBox);
        return checkBox;
    }

    private Label createLabel(final String text, final Pane parent) {
        final Label label = new Label(text);
        parent.getChildren().add(label);
        return label;
    }

    private static String toStringIdentifier(final ReactionType reactionType) {
        return "" + reactionType.getCharacterIdentifier();
    }

}
