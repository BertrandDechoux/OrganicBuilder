package uk.org.squirm3.ui.level;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.model.level.Level;
import uk.org.squirm3.springframework.Messages;

public class CurrentLevelPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JEditorPane description;
    private JButton hintButton, evaluateButton;
    private Level currentLevel;

    private final MessageSource messageSource;

    public CurrentLevelPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource) {
        this.messageSource = messageSource;
        createCurrentLevelPanel(applicationEngine);

        final IListener levelListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                currentLevel = applicationEngine.getLevelManager()
                        .getCurrentLevel();
                if (currentLevel == null) {
                    description.setText(Messages.localize(
                            "level.description.none", messageSource));
                    hintButton.setEnabled(false);
                    evaluateButton.setEnabled(false);
                } else {
                    description.setText("<b>" + currentLevel.getTitle()
                            + "</b>" + currentLevel.getChallenge());
                    if (currentLevel.getHint() == null
                            || currentLevel.getHint().equals("")) {
                        hintButton.setEnabled(false);
                    } else {
                        hintButton.setEnabled(true);
                    }
                    evaluateButton.setEnabled(true);
                }
            }
        };
        levelListener.propertyHasChanged();
        applicationEngine.getEventDispatcher().addListener(levelListener,
                EventDispatcher.Event.LEVEL);
    }

    private void createCurrentLevelPanel(
            final ApplicationEngine applicationEngine) {
        setLayout(new BorderLayout());
        description = new JEditorPane();
        description.setContentType("text/html");
        description.setEditable(false);
        final JScrollPane p = new JScrollPane(description);
        p.setMinimumSize(new Dimension(50, 200));
        add(p, BorderLayout.CENTER);
        add(createButtonsPanel(applicationEngine), BorderLayout.SOUTH);
    }

    private JPanel createButtonsPanel(final ApplicationEngine applicationEngine) {
        final JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.LINE_AXIS));
        hintButton = new JButton(Messages.localize("level.hint", messageSource));
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                JOptionPane.showMessageDialog(CurrentLevelPanel.this,
                        currentLevel.getHint(),
                        Messages.localize("level.hint", messageSource),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        jPanel.add(hintButton);
        jPanel.add(Box.createHorizontalGlue());
        evaluateButton = new JButton(Messages.localize("level.evaluate",
                messageSource));
        evaluateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                String result = currentLevel.evaluate(applicationEngine
                        .getAtoms());
                boolean success = true;
                if (result == null) {
                    result = Messages.localize("level.success", messageSource);
                } else {
                    result = Messages.localize("level.error", messageSource)
                            + result;
                    success = false;
                }
                if (success) {
                    // TODO keep always the same object
                    // TODO store the url into a configuration file
                    final List<? extends Level> levelList = applicationEngine
                            .getLevelManager().getLevels();
                    final int levelNumber = levelList.indexOf(currentLevel);

                    if (levelNumber + 1 > levelList.size() - 1) {
                        result = Messages.localize("level.fullsuccess",
                                messageSource);
                        JOptionPane.showMessageDialog(CurrentLevelPanel.this,
                                result, Messages.localize(
                                        "level.success.title", messageSource),
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        result = Messages.localize("level.success",
                                messageSource);
                        final Object[] options = {
                                Messages.localize("level.yes", messageSource),
                                Messages.localize("level.no", messageSource)};
                        final int n = JOptionPane.showOptionDialog(
                                CurrentLevelPanel.this, result, Messages.localize(
                                        "level.success.title", messageSource),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null, options,
                                options[0]);
                        if (n == JOptionPane.YES_OPTION) {
                            applicationEngine.goToNextLevel();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(CurrentLevelPanel.this, result,
                            Messages.localize("level.error.title",
                                    messageSource), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPanel.add(evaluateButton);
        return jPanel;
    }
}
