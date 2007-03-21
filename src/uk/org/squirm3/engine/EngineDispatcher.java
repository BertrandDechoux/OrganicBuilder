package uk.org.squirm3.engine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


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
