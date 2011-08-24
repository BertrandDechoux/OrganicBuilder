package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Go to the last level.
 */
public class LastLevelAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public LastLevelAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon lastIcon) {
        super(messageSource, applicationEngine, "level.last", lastIcon);
        applicationEngine.addListener(new LastLevelListener(applicationEngine),
                ApplicationEngineEvent.LEVEL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().goToLastLevel();
    }

    /**
     * The action will be active if and only if the current level is not the
     * last level.
     */
    private final class LastLevelListener implements Listener {
        private final ApplicationEngine applicationEngine;

        private LastLevelListener(final ApplicationEngine applicationEngine) {
            this.applicationEngine = applicationEngine;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uk.org.squirm3.listener.Listener#propertyHasChanged()
         */
        @Override
        public void propertyHasChanged() {
            final boolean lastLevel = applicationEngine.getLevelManager()
                    .isCurrentLevelLastLevel();
            setEnabled(!lastLevel);

        }
    }
}
