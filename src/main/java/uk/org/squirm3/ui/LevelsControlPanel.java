package uk.org.squirm3.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.model.level.Level;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.action.IntroAction;
import uk.org.squirm3.ui.action.LastAction;
import uk.org.squirm3.ui.action.NextAction;
import uk.org.squirm3.ui.action.PreviousAction;

public class LevelsControlPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private boolean update;

    public LevelsControlPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final IntroAction introAction,
            final PreviousAction previousAction, final NextAction nextAction,
            final LastAction lastAction) {
        final JComboBox levelComboBox = createLevelComboBox(applicationEngine, messageSource);
        update = true;

        final IListener levelListener = new IListener() {

            @Override
            public void propertyHasChanged() {
                final List<? extends Level> levelList = applicationEngine
                        .getLevelManager().getLevels();
                final int levelNumber = levelList
                        .indexOf(applicationEngine.getLevelManager()
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

        applicationEngine.getEventDispatcher().addListener(levelListener,
                EventDispatcher.Event.LEVEL);

        setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
        setBackground(GUI.BACKGROUND);

        add(GUI.createIconButton(introAction));
        add(GUI.createIconButton(previousAction));

        levelComboBox.setMaximumSize(new Dimension(150, 80));
        add(levelComboBox);

        add(GUI.createIconButton(nextAction));
        add(GUI.createIconButton(lastAction));
    }

    private JComboBox createLevelComboBox(final ApplicationEngine applicationEngine, final MessageSource messageSource) {
        final List<? extends Level> levelList = applicationEngine
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
                    applicationEngine.goToLevel(
                            cb.getSelectedIndex());
                }
                update = false;
            }
        });
        cb.setToolTipText(Messages.localize("navigation.selected",
                messageSource));
        return cb;
    }

}
