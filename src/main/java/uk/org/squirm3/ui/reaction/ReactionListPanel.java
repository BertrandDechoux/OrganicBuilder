package uk.org.squirm3.ui.reaction;

import java.util.Collection;

import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.Utils;
import uk.org.squirm3.ui.reaction.mode.ListViewMode;
import uk.org.squirm3.ui.reaction.mode.ReactionsListMode;
import uk.org.squirm3.ui.reaction.mode.TextAreaMode;

public class ReactionListPanel extends BorderPane {
    private final ApplicationEngine applicationEngine;
    private final MessageSource messageSource;

    private Label nReactions;

    private final ReactionsListMode defaultMode;
    private ReactionsListMode currentMode;

    public ReactionListPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource,
            final ConversionService conversionService) {
        this.applicationEngine = applicationEngine;
        this.messageSource = messageSource;

		currentMode = new TextAreaMode(this, conversionService);
		defaultMode = new ListViewMode(this, currentMode);

		createListPanel();
		setCurrentModeTo(defaultMode);

		applicationEngine.addListener(new ReactionsChangedListener(), ApplicationEngineEvent.REACTIONS);
    }

    public void setCurrentModeTo(final ReactionsListMode futureMode) {
        setRight(futureMode.getMenu());
        setCenter(futureMode.getReactionsList());
        currentMode = futureMode;
        currentMode.reactionsHaveChanged(applicationEngine.getReactions());
    }
    
    public Pane createButtonsPane() {
    	TilePane pane = new TilePane(Orientation.VERTICAL);
    	pane.setVgap(5);
    	pane.setPadding(new Insets(5));
    	pane.setPrefRows(3);
    	return pane;
    }


    public Button createButton(final String key,
            final EventHandler<ActionEvent> handler) {
        final Button button = new Button(Messages.localize(key,
                messageSource));
        Utils.defaultSize(button);
        button.setOnAction(handler);
        return button;
    }

    public void setReactions(final Collection<Reaction> reactions) {
        applicationEngine.setReactions(reactions);
    }

    public void removeReactions(final Collection<Reaction> reactions) {
        applicationEngine.removeReactions(reactions);
    }

    public void clearReactions() {
        applicationEngine.clearReactions();
    }

    public String localize(final String key) {
        return Messages.localize("reactions.current", messageSource);
    }

    private final class ReactionsChangedListener implements Listener {
        @Override
		public void propertyHasChanged() {
			Platform.runLater(() -> {
				final Collection<Reaction> reactions = applicationEngine.getReactions();
				nReactions.setText(localize("reactions.current") + " (" + reactions.size() + ")");
				setCurrentModeTo(defaultMode);
			});
        }
    }

    private void createListPanel() {
		Utils.defaultBorder(this);
        
    	nReactions = new Label(localize("reactions.current"));
    	nReactions.setPadding(new Insets(5));
        
        setTop(nReactions);
        
        setPadding(new Insets(12));
    }
}
