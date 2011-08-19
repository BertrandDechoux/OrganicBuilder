package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class NextLevelAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public NextLevelAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon nextIcon) {
        super(messageSource, applicationEngine, "level.next", nextIcon);

        addListener(new IListener() {
            @Override
            public void propertyHasChanged() {
                final boolean lastLevel = applicationEngine.getLevelManager()
                        .isCurrentLevelLastLevel();
                NextLevelAction.this.setEnabled(!lastLevel);

            }
        }, EventDispatcher.Event.LEVEL);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().goToNextLevel();
    }
}
