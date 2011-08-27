package uk.org.squirm3.ui.toolbar.simulation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Stop the simulation ie the level. The action will be active if and only if
 * the simulation is running.
 */
public class StopSimulationAction extends AbstractAction implements Listener {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;

    public StopSimulationAction(final ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
        applicationEngine.addListener(this,
                ApplicationEngineEvent.SIMULATION_STATE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        applicationEngine.pauseSimulation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.org.squirm3.listener.Listener#propertyHasChanged()
     */
    @Override
    public void propertyHasChanged() {
        setEnabled(applicationEngine.simulationIsRunning());
    }
}
