package uk.org.squirm3.engine;

public interface IEngineListener {

	public void atomsHaveChanged();
	
	public void atomsNumberHasChanged();
	
	public void draggingPointHasChanged();

	public void levelHasChanged();

	public void reactionsHaveChanged();
	
	public void simulationSizeHasChanged();
	
	public void simulationSpeedHasChanged();
	
	public void simulationStateHasChanged();
}
