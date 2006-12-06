package uk.org.squirm3.engine;

import java.util.Collection;

import uk.org.squirm3.data.DraggingPointData;
import uk.org.squirm3.data.Level;

public interface IApplicationEngine {
	
	public void restartLevel();
	public void setLevel(Level newLevel);
	public Level getCurrentLevel();
	
	public Collection getAtoms();
	public void setAtomsNumber(short newAtomsNumber);
	public short getAtomsNumber();
	
	public Collection getReactions();
	public void setReactions(Collection reactions);
	public void addReactions(Collection reactions);
	public void removeReactions(Collection reactions);
	public void clearReactions();
	
	public DraggingPointData getLastUsedDraggingPoint();
	public DraggingPointData getCurrentDraggingPoint();
	public void setDraggingPoint(DraggingPointData newDraggingPointData);
	
	public void setSimulationSpeed(short newSleepPeriod);
	public short getSimulationSpeed();
	
	public void setSimulationSize(int width, int height);
	public int getSimulationWidth();
	public int getSimulationHeight();
	
	public void runSimulation();
	public void pauseSimulation();
	public boolean simulationIsRunning();
	public boolean simulationNeedReset();
	
	public void addListener(IEngineListener l);
	public void removeListener(IEngineListener l);
	
}
