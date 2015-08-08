package uk.org.squirm3.ui.toolbar.navigation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Go to the next level.The action will be active if and only if the current
 * level has a next level.
 */
public class NextLevelAction extends AbstractAction implements Listener {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;

    public NextLevelAction(final ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
        applicationEngine.addListener(this, ApplicationEngineEvent.LEVEL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        applicationEngine.goToNextLevel();
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
