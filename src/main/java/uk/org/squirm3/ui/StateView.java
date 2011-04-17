package uk.org.squirm3.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import uk.org.squirm3.Application;
import uk.org.squirm3.Resource;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class StateView extends AView {

    // Actions controlling the simulation
    private final Action stop, run, reset;

    public StateView(final ApplicationEngine applicationEngine) {
        super(applicationEngine);
        stop = createStopAction();
        run = createRunAction();
        reset = createResetAction();

        final IListener stateListener = new IListener() {
            public void propertyHasChanged() {
                final boolean isRunning = getApplicationEngine()
                        .simulationIsRunning();
                stop.setEnabled(isRunning);
                run.setEnabled(!isRunning);
            }
        };
        stateListener.propertyHasChanged();
        getApplicationEngine().getEventDispatcher().addListener(stateListener,
                EventDispatcher.Event.SIMULATION_STATE);

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
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                getApplicationEngine().runSimulation();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Application.localize("simulation.run"));
        // action.putValue(Action.LONG_DESCRIPTION,
        // "Context-Sensitive Help Text"); TODO and for the others actions too
        action.putValue(Action.SMALL_ICON, Resource.getIcon("play"));
        // action.putValue(Action.MNEMONIC_KEY, new
        // Integer(java.awt.event.KeyEvent.VK_A)); TODO
        return action;
    }

    private Action createStopAction() {
        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                getApplicationEngine().pauseSimulation();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Application.localize("simulation.stop"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("pause"));
        return action;
    }

    private Action createResetAction() {
        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                getApplicationEngine().restartLevel(null);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Application.localize("simulation.reset"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("reset"));
        return action;
    }
}
