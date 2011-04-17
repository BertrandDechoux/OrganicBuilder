package uk.org.squirm3.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;

import uk.org.squirm3.Application;
import uk.org.squirm3.Resource;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class LevelNavigatorView extends AView {

    private final Action intro, previous, next, last;
    private final JComboBox levelComboBox;

    private boolean update;

    public LevelNavigatorView(final ApplicationEngine applicationEngine) {
        super(applicationEngine);

        intro = createIntroAction();
        previous = createPreviousAction();
        levelComboBox = createLevelComboBox();
        next = createNextAction();
        last = createLastAction();
        update = true;

        final IListener levelListener = new IListener() {

            public void propertyHasChanged() {
                final List levelList = getApplicationEngine().getLevelManager()
                        .getLevels();
                final int levelNumber = levelList
                        .indexOf(getApplicationEngine().getLevelManager()
                                .getCurrentLevel());

                final boolean firstLevel = levelNumber == 0;
                intro.setEnabled(!firstLevel);
                previous.setEnabled(!firstLevel);

                final boolean lastLevel = levelNumber == levelList.size() - 1;
                last.setEnabled(!lastLevel);
                next.setEnabled(!lastLevel);

                // quick fix TODO chang
                update = true;
                levelComboBox.setSelectedIndex(levelNumber);
            }
        };
        levelListener.propertyHasChanged();

        getApplicationEngine().getEventDispatcher().addListener(levelListener,
                EventDispatcher.Event.LEVEL);

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
        final List levelList = getApplicationEngine().getLevelManager()
                .getLevels();
        final String[] levelsLabels = new String[levelList.size()];
        final Iterator it = levelList.iterator();
        int i = 0;
        while (it.hasNext()) {
            String number = String.valueOf(i) + "  ";
            if (i < 10) {
                number += "  ";
            }
            levelsLabels[i] = number + ((Level) it.next()).getTitle();
            i++;
        }
        final JComboBox cb = new JComboBox(levelsLabels);
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                if (!update) {
                    getApplicationEngine().goToLevel(
                            levelComboBox.getSelectedIndex(), null);
                }
                update = false;
            }
        });
        cb.setToolTipText(Application.localize("navigation.selected"));
        return cb;
    }

    private Action createIntroAction() {
        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                getApplicationEngine().goToFirstLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Application.localize("navigation.first"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("first"));
        return action;
    }

    private Action createPreviousAction() {
        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                getApplicationEngine().goToPreviousLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Application.localize("navigation.previous"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("previous"));
        return action;
    }

    private Action createNextAction() {
        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                getApplicationEngine().goToNextLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Application.localize("navigation.next"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("next"));
        return action;
    }

    private Action createLastAction() {
        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                getApplicationEngine().goToLastLevel();
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Application.localize("navigation.last"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("last"));
        return action;
    }
}
