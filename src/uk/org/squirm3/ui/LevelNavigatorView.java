package uk.org.squirm3.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.ILevelListener;


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

public class LevelNavigatorView implements IView, ILevelListener {
	private ApplicationEngine applicationEngine;
	
	private final Action intro, previous, next, last;
	private final JComboBox levelComboBox;
	
	
	public LevelNavigatorView(ApplicationEngine applicationEngine) {
		this.applicationEngine = applicationEngine;
		intro = createIntroAction();
		previous = createPreviousAction();
		levelComboBox = createLevelComboBox();
		next = createNextAction();
		last = createLastAction();
		applicationEngine.getEngineDispatcher().addLevelListener(this);
		levelHasChanged();
	}
	
	public Action getIntroAction() {
		return intro;
	}
	
	public Action getPreviousAction() {
		return previous;
	}
	
	public Action getNextAction() {
		return next;
	}
	
	public Action getLastAction() {
		return last;
	}
	
	public JComboBox getLevelComboBox() {
		return levelComboBox;
	}
	
	private JComboBox createLevelComboBox() {
		List levelList = applicationEngine.getLevels();
		String[] levelsLabels = new String[levelList.size()];
		Iterator it = levelList.iterator();
		int i = 0;
		while(it.hasNext()) {
			String number = String.valueOf(i)+"  ";
			if(i<10) number += "  ";
			levelsLabels[i] = number+((Level)it.next()).getTitle();
			i++;
		}
		final JComboBox cb = new JComboBox(levelsLabels);
		cb.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						applicationEngine.goToLevel(levelComboBox.getSelectedIndex(),null);
					}
				});
		cb.setToolTipText(Application.localize(new String[] {"interface","navigation","selected"}));
		return cb;
	}
	
	private Action createIntroAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				applicationEngine.goToFirstLevel();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","first"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("first"));
		return 	action;
	}
	
	private Action createPreviousAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				applicationEngine.goToPreviousLevel();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","previous"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("previous"));
		return 	action;
	}
	
	private Action createNextAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				applicationEngine.goToNextLevel();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","next"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("next"));
		return 	action;
	}
	
	private Action createLastAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				applicationEngine.goToLastLevel();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","navigation","last"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("last"));
		return 	action;
	}

	private void updateControls() {
		final List levelList = applicationEngine.getLevels();
		final int levelNumber = applicationEngine.getLevels().indexOf(applicationEngine.getCurrentLevel());
		
		final boolean firstLevel = levelNumber==0;
		intro.setEnabled(!firstLevel);
		previous.setEnabled(!firstLevel);

		final boolean lastLevel = levelNumber==(levelList.size()-1);
		last.setEnabled(!lastLevel);
		next.setEnabled(!lastLevel);

		levelComboBox.setSelectedIndex(levelNumber);
	}

	public void levelHasChanged() {
		updateControls();
	}
	
	public void configurationHasChanged() {}
}
