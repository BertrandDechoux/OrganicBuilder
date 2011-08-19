package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher.Event;
import uk.org.squirm3.listener.IListener;

public class StopSimulationAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    public StopSimulationAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon previousIcon) {
        super(messageSource, applicationEngine, "simulation.stop", previousIcon);

        addListener(new IListener() {
            @Override
            public void propertyHasChanged() {
                StopSimulationAction.this.setEnabled(applicationEngine
                        .simulationIsRunning());
            }
        }, Event.SIMULATION_STATE);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        getApplicationEngine().pauseSimulation();
    }
}
