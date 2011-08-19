package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class PreviousLevelAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public PreviousLevelAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon previousIcon) {
        super(messageSource, applicationEngine, "level.previous",
                previousIcon);

        addListener(new IListener() {
            @Override
            public void propertyHasChanged() {
                final boolean firstLevel = applicationEngine.getLevelManager()
                        .isCurrentLevelFirstLevel();
                PreviousLevelAction.this.setEnabled(!firstLevel);

            }
        }, EventDispatcher.Event.LEVEL);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().goToPreviousLevel();
    }
}
