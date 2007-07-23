package uk.org.squirm3.engine;

import java.util.Collection;
import java.util.List;

import uk.org.squirm3.data.DraggingPoint;
import uk.org.squirm3.data.Level;


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


public interface IApplicationEngine {
	
	public void goToLevel(int levelIndex);
	public void goToFirstLevel();
	public void goToLastLevel();
	public void goToNextLevel();
	public void goToPreviousLevel();
	public void restartLevel();
	public Level getCurrentLevel();
	public List getLevels();
	
	public Collection getAtoms();
	public void setAtomsNumber(short newAtomsNumber);
	public short getAtomsNumber();
	
	public Collection getReactions();
	public void setReactions(Collection reactions);
	public void addReactions(Collection reactions);
	public void removeReactions(Collection reactions);
	public void clearReactions();
	
	public DraggingPoint getLastUsedDraggingPoint();
	public DraggingPoint getCurrentDraggingPoint();
	public void setDraggingPoint(DraggingPoint newDraggingPoint);
	
	public void setSimulationSpeed(short newSleepPeriod);
	public short getSimulationSpeed();
	
	public void setSimulationSize(int width, int height);
	public int getSimulationWidth();
	public int getSimulationHeight();
	
	public void runSimulation();
	public void pauseSimulation();
	public boolean simulationIsRunning();
	public boolean simulationNeedReset();
	
	public void addEngineListener(IEngineListener listener);
	public void removeEngineListener(IEngineListener listener);

    public void addAtomListener(IAtomListener listener);
    public void removeAtomListener(IAtomListener listener);

    public void addLevelListener(ILevelListener listener);
    public void removeLevelListener(ILevelListener listener);
    
    public void addPropertyListener(IPropertyListener listener);
    public void removePropertyListener(IPropertyListener listener);
    
    public void addReactionListener(IReactionListener listener);
    public void removeReactionListener(IReactionListener listener);

    public void addStateListener(IStateListener listener);
    public void removeStateListener(IStateListener listener);
	
}
