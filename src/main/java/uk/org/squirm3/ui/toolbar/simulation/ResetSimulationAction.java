package uk.org.squirm3.ui.toolbar.simulation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import uk.org.squirm3.engine.ApplicationEngine;

/**
 * Restart the simulation ie the level.
 */
public class ResetSimulationAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;

    public ResetSimulationAction(final ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        applicationEngine.restartLevel();
    }
}
