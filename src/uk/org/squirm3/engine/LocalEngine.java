package uk.org.squirm3.engine;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.DraggingPointData;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.Reaction;

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

//TODO synchronized ?
public class LocalEngine implements IApplicationEngine {
	
	private final EngineDispatcher engineDispatcher;
	
	private final Collider collider;
	private Thread thread;
	// simulation's attributes
	private int simulationHeight;
	private int simulationWidth;
	public boolean resetNeeded;
	private short sleepPeriod; // how many milliseconds to sleep for each iteration (user changeable)
	// things to do with the dragging around of atoms
	private DraggingPointData draggingPointData;
	private DraggingPointData lastUsedDraggingPoint;
	private Level currentLevel;
	
	public LocalEngine(){	//TODO values should'nt be hardcoded, properties files ?
		engineDispatcher = new EngineDispatcher();
		simulationHeight = 500;
		simulationWidth = 500;
		resetNeeded = false;
		sleepPeriod = 50;
		collider = new Collider(50, simulationWidth, simulationHeight);
	}

	public void clearReactions() {
		pauseSimulation();
		Vector reactions = collider.getReactions();
		reactions.clear();
		engineDispatcher.reactionsHaveChanged();
		runSimulation();
	}

	public Collection getAtoms() {
		Atom[] atoms = collider.getAtoms();
		List list = new LinkedList();
		for(int i = 0 ; i < atoms.length ; i++) {
			list.add(atoms[i]);
		}
		return list;
	}

	public short getAtomsNumber() {
		return (short)collider.getNumAtoms();
	}

	public DraggingPointData getCurrentDraggingPoint() {
		return draggingPointData;
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public DraggingPointData getLastUsedDraggingPoint() {
		return lastUsedDraggingPoint;
	}

	public Collection getReactions() {
		Vector reactions = collider.getReactions();
		List list = new LinkedList();
		list.addAll(reactions);
		return list;
	}

	public int getSimulationHeight() {
		return simulationHeight;
	}

	public short getSimulationSpeed() {
		return sleepPeriod;
	}

	public int getSimulationWidth() {
		return simulationWidth;
	}

	public void pauseSimulation() {
		Thread target = thread;
		thread = null;
		if(target!=null) {
			try {
				target.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		engineDispatcher.simulationStateHasChanged();
	}
	
	public void addReactions(Collection reactions) {
		pauseSimulation();
		Vector colliderReactions = collider.getReactions();
		colliderReactions.addAll(reactions);
		engineDispatcher.reactionsHaveChanged();
		runSimulation();	
	}

	public void removeReactions(Collection reactions) {
		pauseSimulation();
		Vector colliderReactions = collider.getReactions();
		colliderReactions.removeAll(reactions);
		engineDispatcher.reactionsHaveChanged();
		runSimulation();
	}

	public void restartLevel() {
		pauseSimulation();
	  	int nAtoms = collider.getNumAtoms();
		Atom[] newAtoms = currentLevel.resetAtoms(nAtoms, simulationWidth, simulationHeight);
		collider.setAtoms(newAtoms, simulationWidth, simulationHeight);
		engineDispatcher.atomsHaveChanged();
		needToRestartLevel(false);
	}
	
	private void needToRestartLevel(boolean b) {
		resetNeeded = b;
		engineDispatcher.simulationStateHasChanged();
	}

	public void runSimulation() {
		if(thread!=null || resetNeeded) return; // security check to avoid starting if already started
		thread = new Thread(
			new Runnable(){
				public void run()  {
					while (thread == Thread.currentThread()) {
						lastUsedDraggingPoint = draggingPointData;
						collider.doTimeStep(simulationWidth, simulationHeight, draggingPointData);
						engineDispatcher.atomsHaveChanged();
						try {
							Thread.sleep(sleepPeriod);
						} catch (InterruptedException e) { break; }
					}
				}
			});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		engineDispatcher.simulationStateHasChanged();
	}
	
	

	public void setAtomsNumber(short newAtomsNumber) {
		needToRestartLevel(true);
		pauseSimulation();
		collider.setNAtoms(newAtomsNumber);
		engineDispatcher.atomsNumberHasChanged();
	}

	public void setDraggingPoint(DraggingPointData newDraggingPointData) {
		if(draggingPointData==null && newDraggingPointData==null) return;
		if(draggingPointData==null && newDraggingPointData!=null
				|| draggingPointData!=null && newDraggingPointData==null) {
			draggingPointData = newDraggingPointData;
			engineDispatcher.draggingPointHasChanged();
			return;
		}
		if(draggingPointData.equals(newDraggingPointData)) return;
		draggingPointData = newDraggingPointData;
		engineDispatcher.draggingPointHasChanged();
	}

	public void setLevel(Level newLevel) {
		pauseSimulation();
		currentLevel = newLevel;
		collider.setReactions(new Reaction[0]);
		engineDispatcher.reactionsHaveChanged();
	  	int nAtoms = collider.getNumAtoms();
		Atom[] newAtoms = currentLevel.resetAtoms(nAtoms, simulationWidth, simulationHeight);
		collider.setAtoms(newAtoms, simulationWidth, simulationHeight);
		engineDispatcher.atomsHaveChanged();
		engineDispatcher.levelHasChanged();
		runSimulation();
	}

	public void setReactions(Collection reactions) {
		pauseSimulation();
		Vector colliderReactions = collider.getReactions();
		colliderReactions.clear();
		colliderReactions.addAll(reactions);
		engineDispatcher.reactionsHaveChanged();
		runSimulation();
	}

	public void setSimulationSize(int width, int height) {
		simulationWidth = width;
		simulationHeight = height;
		engineDispatcher.simulationSizeHasChanged();
	}

	public void setSimulationSpeed(short newSleepPeriod) {
		sleepPeriod = newSleepPeriod;
		engineDispatcher.simulationSpeedHasChanged();
	}

	public boolean simulationIsRunning() {
		return thread!=null;
	}

	public boolean simulationNeedReset() {
		return resetNeeded;
	}

	public void addListener(IEngineListener l) {
		engineDispatcher.addListener(l);	
	}

	public void removeListener(IEngineListener l) {
		engineDispatcher.removeListener(l);
	}


}
