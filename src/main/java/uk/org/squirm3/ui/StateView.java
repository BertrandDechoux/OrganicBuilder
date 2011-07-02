package uk.org.squirm3.ui;

import javax.swing.Action;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class StateView extends AView {

    // Actions controlling the simulation
    private final Action stopAction, runAction, resetAction;

    public StateView(final ApplicationEngine applicationEngine, final Action stopAction, final Action runAction, final Action resetAction) {
        super(applicationEngine);
        this.stopAction = stopAction;
        this.runAction = runAction;
        this.resetAction = resetAction;

        final IListener stateListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                final boolean isRunning = getApplicationEngine()
                        .simulationIsRunning();
                stopAction.setEnabled(isRunning);
                runAction.setEnabled(!isRunning);
            }
        };
        stateListener.propertyHasChanged();
        getApplicationEngine().getEventDispatcher().addListener(stateListener,
                EventDispatcher.Event.SIMULATION_STATE);

    }

    public Action getRunAction() {
        return runAction;
    }

    public Action getStopAction() {
        return stopAction;
    }

    public Action getResetAction() {
        return resetAction;
    }
}
