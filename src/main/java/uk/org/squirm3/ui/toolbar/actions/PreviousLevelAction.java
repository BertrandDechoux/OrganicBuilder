package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Go to previous level.
 */
public class PreviousLevelAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public PreviousLevelAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon previousIcon) {
        super(messageSource, applicationEngine, "level.previous", previousIcon);
        applicationEngine.addListener(new PreviousLevelListener(
                applicationEngine), ApplicationEngineEvent.LEVEL);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().goToPreviousLevel();
    }

    /**
     * The action will be active if and only if the current level is has a
     * previous level.
     */
    private final class PreviousLevelListener implements Listener {
        private final ApplicationEngine applicationEngine;

        private PreviousLevelListener(final ApplicationEngine applicationEngine) {
            this.applicationEngine = applicationEngine;
        }

        /*
         * (non-Javadoc)
         * 
         * @see uk.org.squirm3.listener.Listener#propertyHasChanged()
         */
        @Override
        public void propertyHasChanged() {
            final boolean firstLevel = applicationEngine.getLevelManager()
                    .isCurrentLevelFirstLevel();
            setEnabled(!firstLevel);

        }
    }
}
