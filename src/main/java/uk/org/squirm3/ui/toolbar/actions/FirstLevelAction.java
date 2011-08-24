package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Go to the first level.
 */
public class FirstLevelAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public FirstLevelAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon firstIcon) {
        super(messageSource, applicationEngine, "level.first", firstIcon);
        applicationEngine.addListener(
                new FirstLevelListener(applicationEngine),
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
        getApplicationEngine().goToFirstLevel();
    }

    /**
     * The action will be active if and only if the current level is not the
     * first level.
     */
    private final class FirstLevelListener implements Listener {
        private final ApplicationEngine applicationEngine;

        private FirstLevelListener(final ApplicationEngine applicationEngine) {
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
