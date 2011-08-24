package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;

/**
 * Restart the simulation ie the level.
 */
public class ResetSimulationAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public ResetSimulationAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon resetIcon) {
        super(messageSource, applicationEngine, "simulation.reset", resetIcon);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().restartLevel();
    }
}
