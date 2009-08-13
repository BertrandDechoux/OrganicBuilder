package uk.org.squirm3.ui;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.IStateListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ${my.copyright}
 */

public class StateView implements IView, IStateListener {

    // Actions controlling the simulation
    private final Action stop, run, reset;
    // use to communicate
    private ApplicationEngine applicationEngine;

    public StateView(ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
        stop = createStopAction();
        run = createRunAction();
        reset = createResetAction();
        simulationStateHasChanged();
        applicationEngine.getEngineDispatcher().addStateListener(this);
    }

    public Action getRunAction() {
        return run;
    }

    public Action getStopAction() {
        return stop;
    }

    public Action getResetAction() {
        return reset;
    }

    private Action createRunAction() {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                applicationEngine.runSimulation();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize("simulation.run"));
        //action.putValue(Action.LONG_DESCRIPTION, "Context-Sensitive Help Text"); TODO and for the others actions too
        action.putValue(Action.SMALL_ICON, Resource.getIcon("play"));
        //action.putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_A)); TODO
        return action;
    }

    private Action createStopAction() {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                applicationEngine.pauseSimulation();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize("simulation.stop"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("pause"));
        return action;
    }

    private Action createResetAction() {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                applicationEngine.restartLevel(null);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize("simulation.reset"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("reset"));
        return action;
    }

    public void simulationStateHasChanged() {
        boolean isRunning = applicationEngine.simulationIsRunning();
        stop.setEnabled(isRunning);
        run.setEnabled(!isRunning);
    }
}
