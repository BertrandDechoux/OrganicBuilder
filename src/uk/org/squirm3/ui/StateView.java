package uk.org.squirm3.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.IStateListener;


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
along with Foobar; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class StateView implements IView, IStateListener {
	
	// Actions controlling the simulation
	private final Action stop, run, reset;
	// use to communicate
	private IApplicationEngine iApplicationEngine;

	public StateView(IApplicationEngine iApplicationEngine) {
		this.iApplicationEngine = iApplicationEngine;
		stop = createStopAction();
		run = createRunAction();
		reset = createResetAction();
		simulationStateHasChanged();
		iApplicationEngine.addStateListener(this);
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
				iApplicationEngine.runSimulation();
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
				iApplicationEngine.pauseSimulation();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","stop"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("pause"));
		return 	action;
	}
	
	private Action createResetAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				iApplicationEngine.restartLevel();;
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","reset"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("reset"));
		return 	action;
	}
	
	public void simulationStateHasChanged() {
		boolean isRunning = iApplicationEngine.simulationIsRunning();
		boolean resetNeeded = iApplicationEngine.simulationNeedReset();
		stop.setEnabled(!resetNeeded && isRunning);
		run.setEnabled(!resetNeeded && !isRunning);
	}

	public void isVisible(boolean b) {
		// TODO Auto-generated method stub
	}

}
