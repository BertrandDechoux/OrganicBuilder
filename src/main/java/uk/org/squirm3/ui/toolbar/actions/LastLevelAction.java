package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class LastLevelAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public LastLevelAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon lastIcon) {
        super(messageSource, applicationEngine, "level.last", lastIcon);

        addListener(new IListener() {
            @Override
            public void propertyHasChanged() {
                final boolean lastLevel = applicationEngine.getLevelManager()
                        .isCurrentLevelLastLevel();
                LastLevelAction.this.setEnabled(!lastLevel);

            }
        }, EventDispatcher.Event.LEVEL);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().goToLastLevel();
    }
}
