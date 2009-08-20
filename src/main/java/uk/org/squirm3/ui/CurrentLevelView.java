package uk.org.squirm3.ui;

import uk.org.squirm3.Application;
import uk.org.squirm3.ILogger;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.ILevel;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.listener.EventDispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * ${my.copyright}
 */

public class CurrentLevelView extends AView {
    private JEditorPane description;
    private JButton hintButton, evaluateButton;
    private ILevel currentLevel;

    private final JPanel currentLevelPanel;

    private final ILogger logger;

    public CurrentLevelView(ApplicationEngine applicationEngine, ILogger loggerUrl) {
        super(applicationEngine);
        currentLevelPanel = createCurrentLevelPanel();
        logger = loggerUrl;

        IListener levelListener = new IListener() {
            public void propertyHasChanged() {
                currentLevel = getApplicationEngine().getLevelManager().getCurrentLevel();
                if (currentLevel == null) {
                    description.setText(Application.localize("level.description.none"));
                    hintButton.setEnabled(false);
                    evaluateButton.setEnabled(false);
                } else {
                    description.setText("<b>" + currentLevel.getTitle() + "</b>" + currentLevel.getChallenge());
                    if (currentLevel.getHint() == null || currentLevel.getHint().equals("")) {
                        hintButton.setEnabled(false);
                    } else {
                        hintButton.setEnabled(true);
                    }
                    evaluateButton.setEnabled(true);
                }
            }
        };
        levelListener.propertyHasChanged();
        getApplicationEngine().getEventDispatcher().addListener(levelListener,
                EventDispatcher.Event.LEVEL);
    }

    private JPanel createCurrentLevelPanel() {
        final JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        description = new JEditorPane();
        description.setContentType("text/html");
        description.setEditable(false);
        JScrollPane p = new JScrollPane(description);
        p.setMinimumSize(new Dimension(50, 200));
        jPanel.add(p, BorderLayout.CENTER);
        jPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        return jPanel;
    }

    public JPanel getCurrentLevelPanel() {
        return currentLevelPanel;
    }

    private JPanel createButtonsPanel() {
        final JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.LINE_AXIS));
        hintButton = new JButton(Application.localize("level.hint"));
        hintButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JOptionPane.showMessageDialog(currentLevelPanel, currentLevel.getHint(), Application.localize("level.hint"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        jPanel.add(hintButton);
        jPanel.add(Box.createHorizontalGlue());
        evaluateButton = new JButton(Application.localize("level.evaluate"));
        evaluateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String result = currentLevel.evaluate(
                        getApplicationEngine().getAtoms());
                boolean success = true;
                if (result == null) {
                    result = Application.localize("level.success");
                } else {
                    result = Application.localize("level.error") + result;
                    success = false;
                }
                if (success) {
                    //TODO keep always the same object
                    // TODO store the url into a configuration file
                    List levelList = getApplicationEngine().getLevelManager().getLevels();
                    final int levelNumber = levelList.indexOf(currentLevel);
                    logger.writeSolution(levelNumber, getApplicationEngine().getReactions());

                    if (levelNumber + 1 > levelList.size() - 1) {
                        result = Application.localize("level.fullsuccess");
                        JOptionPane.showMessageDialog(currentLevelPanel, result,
                                Application.localize("level.success.title"),
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        result = Application.localize("level.success");
                        Object[] options = {Application.localize("level.yes"),
                                Application.localize("level.no")};
                        int n = JOptionPane.showOptionDialog(currentLevelPanel,
                                result, Application.localize("level.success.title"),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                options[0]);
                        if (n == JOptionPane.YES_OPTION) getApplicationEngine().goToNextLevel();
                    }
                } else {
                    JOptionPane.showMessageDialog(currentLevelPanel, result,
                            Application.localize("level.error.title"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPanel.add(evaluateButton);
        return jPanel;
    }
}

