package uk.org.squirm3.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.IStateListener;


/**  
Copyright 2007 Tim J. Hutton, Ralph Hartley, Bertrand Dechoux

This file is part of Organic Builder.

Organic Builder is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Organic Builder is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Organic Builder; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","run"}));
	    //action.putValue(Action.LONG_DESCRIPTION, "Context-Sensitive Help Text"); TODO and for the others actions too
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("play"));
	    //action.putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_A)); TODO
		return 	action;
	}
	
	private Action createStopAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				applicationEngine.pauseSimulation();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","stop"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("pause"));
		return 	action;
	}
	
	private Action createResetAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				applicationEngine.restartLevel(null);
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","reset"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("reset"));
		return 	action;
	}
	
	public void simulationStateHasChanged() {
		boolean isRunning = applicationEngine.simulationIsRunning();
		boolean resetNeeded = applicationEngine.simulationNeedReset();
		stop.setEnabled(!resetNeeded && isRunning);
		run.setEnabled(!resetNeeded && !isRunning);
	}
}
