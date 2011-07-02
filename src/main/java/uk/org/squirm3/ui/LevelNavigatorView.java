package uk.org.squirm3.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComboBox;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.model.level.Level;

public class LevelNavigatorView extends AView {

    private final Action introAction, previousAction, nextAction, lastAction;
    private final JComboBox levelComboBox;

    private boolean update;

    public LevelNavigatorView(final MessageSource messageSource, final ApplicationEngine applicationEngine, final Action introAction, final Action previousAction, final Action nextAction, final Action lastAction) {
        super(applicationEngine);

        this.introAction = introAction;
        this.previousAction = previousAction;
        this.nextAction = nextAction;
        this.lastAction = lastAction;
        levelComboBox = createLevelComboBox(messageSource);
        update = true;

        final IListener levelListener = new IListener() {

            @Override
            public void propertyHasChanged() {
                final List<? extends Level> levelList = getApplicationEngine()
                        .getLevelManager().getLevels();
                final int levelNumber = levelList
                        .indexOf(getApplicationEngine().getLevelManager()
                                .getCurrentLevel());

                final boolean firstLevel = levelNumber == 0;
                introAction.setEnabled(!firstLevel);
                previousAction.setEnabled(!firstLevel);

                final boolean lastLevel = levelNumber == levelList.size() - 1;
                lastAction.setEnabled(!lastLevel);
                nextAction.setEnabled(!lastLevel);

                update = true;
                levelComboBox.setSelectedIndex(levelNumber);
            }
        };
        levelListener.propertyHasChanged();

        getApplicationEngine().getEventDispatcher().addListener(levelListener,
                EventDispatcher.Event.LEVEL);

    }

    public Action getIntroAction() {
        return introAction;
    }

    public Action getPreviousAction() {
        return previousAction;
    }

    public Action getNextAction() {
        return nextAction;
    }

    public Action getLastAction() {
        return lastAction;
    }

    public JComboBox getLevelComboBox() {
        return levelComboBox;
    }

    private JComboBox createLevelComboBox(final MessageSource messageSource) {
        final List<? extends Level> levelList = getApplicationEngine()
                .getLevelManager().getLevels();
        final String[] levelsLabels = new String[levelList.size()];
        final Iterator<? extends Level> it = levelList.iterator();
        int i = 0;
        while (it.hasNext()) {
            String number = String.valueOf(i) + "  ";
            if (i < 10) {
                number += "  ";
            }
            levelsLabels[i] = number + it.next().getTitle();
            i++;
        }
        final JComboBox cb = new JComboBox(levelsLabels);
        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!update) {
                    getApplicationEngine().goToLevel(
                            levelComboBox.getSelectedIndex(), null);
                }
                update = false;
            }
        });
        cb.setToolTipText(Messages.localize("navigation.selected", messageSource));
        return cb;
    }

}
