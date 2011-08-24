package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Go to the next level.
 */
public class NextLevelAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public NextLevelAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon nextIcon) {
        super(messageSource, applicationEngine, "level.next", nextIcon);
        applicationEngine.addListener(new NextLevelListener(applicationEngine),
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
        getApplicationEngine().goToNextLevel();
    }

    /**
     * The action will be active if and only if the current level has a next
     * level.
     */
    private final class NextLevelListener implements Listener {
        private final ApplicationEngine applicationEngine;

        private NextLevelListener(final ApplicationEngine applicationEngine) {
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
