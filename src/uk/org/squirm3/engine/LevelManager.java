package uk.org.squirm3.engine;

import java.util.ArrayList;
import java.util.List;

import uk.org.squirm3.data.Level;

/**  
Copyright 2007  Bertrand Dechoux

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

public final class LevelManager {
	private Level currentLevel;
	private final List levelList;
	private int levelIndex;
	
	public LevelManager() {
		levelList = new ArrayList();
		levelIndex = -1;
		currentLevel = null;
	}
	
	protected void addLevel(Level l) {
		levelList.add(l);
		if(levelIndex==-1) {
			levelIndex = 0;
			currentLevel = l;
		}
	}
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	public int getCurrentLevelIndex() {
		return levelIndex;
	}
	
	public List getLevels() {
		return new ArrayList(levelList);
	}
	
	public int getNumberOfLevel() {
		return levelList.size();
	}
	
	protected void setLevel(int index) {
		if(index>levelList.size()) index = levelList.size()-1;
		if(index<0) return;
		levelIndex = index;
		currentLevel = (Level)levelList.get(index);
	}	
}
