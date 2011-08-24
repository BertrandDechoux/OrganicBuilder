package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;

/**
 * Run the simulation ie the level.
 */
public class RunSimulationAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public RunSimulationAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon runIcon) {
        super(messageSource, applicationEngine, "simulation.run", runIcon);
        applicationEngine.addListener(new SimulationIsRunningListener(
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
        getApplicationEngine().runSimulation();
    }

    /**
     * The action will be active if and only if the simulation has been stopped.
     */
    private final class SimulationIsRunningListener implements Listener {
        private final ApplicationEngine applicationEngine;

        private SimulationIsRunningListener(
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
            setEnabled(!applicationEngine.simulationIsRunning());
        }
    }
}
