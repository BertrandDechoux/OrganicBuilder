package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class RunSimulationAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public RunSimulationAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon runIcon) {
        super(messageSource, applicationEngine, "simulation.run", runIcon);

        addListener(new IListener() {
            @Override
            public void propertyHasChanged() {
                RunSimulationAction.this.setEnabled(!applicationEngine
                        .simulationIsRunning());
            }
        }, EventDispatcher.Event.SIMULATION_STATE);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().runSimulation();
    }
}
