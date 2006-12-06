package uk.org.squirm3.engine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class EngineDispatcher implements IEngineListener {
	
	private List iEngineListeners = new LinkedList();
	
	public void addListener(IEngineListener l) {
		iEngineListeners.add(l);
	}
	
	public void removeListener(IEngineListener l) {
		iEngineListeners.remove(l);
	}

	public void atomsHaveChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).atomsHaveChanged();
		}
	}

	public void atomsNumberHasChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).atomsNumberHasChanged();
		}
	}

	public void draggingPointHasChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).draggingPointHasChanged();
		}
	}

	public void levelHasChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).levelHasChanged();
		}
	}

	public void reactionsHaveChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).reactionsHaveChanged();
		}
	}

	public void simulationSizeHasChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).simulationSizeHasChanged();
		}
	}

	public void simulationSpeedHasChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).simulationSpeedHasChanged();
		}
	}

	public void simulationStateHasChanged() {
		Iterator it = iEngineListeners.iterator();
		while(it.hasNext()) {
			((IEngineListener)it.next()).simulationStateHasChanged();
		}
	}

}
