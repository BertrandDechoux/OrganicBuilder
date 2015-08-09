package uk.org.squirm3.ui.reaction;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.reaction.mode.ListViewMode;
import uk.org.squirm3.ui.reaction.mode.ReactionsListMode;
import uk.org.squirm3.ui.reaction.mode.TextAreaMode;

public class ReactionListPanel extends JFXPanel {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;
    private final MessageSource messageSource;

    private BorderPane buttonParentPanel;
    private ScrollPane scrollPanel;
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

        Platform.runLater(() -> {
			BorderPane mainPane = new BorderPane();
			Scene scene = new Scene(mainPane);
			scene.setFill(Color.BLACK);

			ReactionListPanel.this.setScene(scene);
			
        	createListPanel(mainPane);
        	setCurrentModeTo(defaultMode);

        	applicationEngine.addListener(new ReactionsChangedListener(),
        			ApplicationEngineEvent.REACTIONS);
        });
    }

    public void setCurrentModeTo(final ReactionsListMode futureMode) {
        buttonParentPanel.setTop(futureMode.getMenu());
        scrollPanel.setContent(futureMode.getReactionsList());
        currentMode = futureMode;
        currentMode.reactionsHaveChanged(applicationEngine.getReactions());
    }

    public Button createButton(final String key,
            final EventHandler<ActionEvent> handler) {
        final Button button = new Button(Messages.localize(key,
                messageSource));
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
				ReactionListPanel.this.repaint();
			});
        }
    }

    private void createListPanel(BorderPane mainPane) {
    	mainPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    	
        nReactions = new Label(localize("reactions.current"));
        mainPane.setTop(nReactions);
        
        buttonParentPanel = new BorderPane();
        mainPane.setRight(buttonParentPanel);
        
        scrollPanel = new ScrollPane();
        mainPane.setCenter(scrollPanel);
    }
}
