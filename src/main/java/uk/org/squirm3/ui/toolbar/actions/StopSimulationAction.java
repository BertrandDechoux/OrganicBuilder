package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Stop the simulation ie the level.
 */
public class StopSimulationAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public StopSimulationAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon previousIcon) {
        super(messageSource, applicationEngine, "simulation.stop", previousIcon);
        applicationEngine.addListener(new SimulationIsStoppedListener(
                applicationEngine), ApplicationEngineEvent.SIMULATION_STATE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().pauseSimulation();
    }

    /**
     * The action will be active if and only if the simulation is running.
     */
    private final class SimulationIsStoppedListener implements Listener {
        private final ApplicationEngine applicationEngine;
        private SimulationIsStoppedListener(
                final ApplicationEngine applicationEngine) {
            this.applicationEngine = applicationEngine;
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
}
