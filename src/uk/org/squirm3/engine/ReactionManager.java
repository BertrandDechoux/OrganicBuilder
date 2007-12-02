package uk.org.squirm3.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.org.squirm3.data.Reaction;

/**  
Copyright 2007 Bertrand Dechoux

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

public class ReactionManager {
	private List reactions = new LinkedList();
	private List Immutablereactions = Collections.unmodifiableList(reactions);

	public void addReaction(Reaction r) {
		reactions.add(r);
	}

	public void addReactions(Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			if(o instanceof Reaction) {
				Reaction r = (Reaction)o;
				reactions.add(r);
			}
		}
	}
	
	public void removeReactions(Reaction r) {
		reactions.remove(r);
	}
	
	public void removeReactions(Collection c) {
		Iterator it = c.iterator();
		while(it.hasNext()) {
			Object o = it.next();
			if(o instanceof Reaction) {
				Reaction r = (Reaction)o;
				reactions.remove(r);
			}
		}
	}
	
	public void clearReactions() {
		reactions.clear();
	}
	
	public List getReactions() {
		return Immutablereactions;
	}
}
