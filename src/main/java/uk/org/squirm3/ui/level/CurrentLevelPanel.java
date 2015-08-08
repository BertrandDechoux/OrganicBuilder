package uk.org.squirm3.ui.level;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.level.Level;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.swing.SwingUtils;

public class CurrentLevelPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JEditorPane description;
    private JButton hintButton, evaluateButton;
    private Level currentLevel;

    private final MessageSource messageSource;

    public CurrentLevelPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource) {
        this.messageSource = messageSource;
        description = SwingUtils.createReadOnlyHtmlEditorPane();

        setLayout(new BorderLayout());
        add(decorateWithScroll(description), BorderLayout.CENTER);
        add(createButtonsPanel(applicationEngine), BorderLayout.SOUTH);

        bindWithApplicationEngine(applicationEngine, messageSource);
    }

    private void bindWithApplicationEngine(
            final ApplicationEngine applicationEngine,
            final MessageSource messageSource) {
        applicationEngine.addListener(new LevelListener(applicationEngine,
                messageSource), ApplicationEngineEvent.LEVEL);
    }

    private JScrollPane decorateWithScroll(final Component component) {
        final JScrollPane pane = new JScrollPane(component);
        pane.setMinimumSize(new Dimension(50, 200));
        return pane;
    }

    private JPanel createButtonsPanel(final ApplicationEngine applicationEngine) {
        final JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.LINE_AXIS));
        hintButton = createJButton("level.hint", new HintListener(), jPanel);
        jPanel.add(Box.createHorizontalGlue());
        evaluateButton = createJButton("level.evaluate", new EvaluateListener(
                applicationEngine), jPanel);
        return jPanel;
    }

    private JButton createJButton(final String key,
            final ActionListener listener, final JPanel parent) {
        final JButton jButton = new JButton(localize(key));
        jButton.addActionListener(listener);
        parent.add(jButton);
        return jButton;
    }

    private String localize(final String key) {
        return Messages.localize(key, messageSource);
    }

    private final class EvaluateListener implements ActionListener {
        private final Object[] options;
        private final ApplicationEngine applicationEngine;

        private EvaluateListener(final ApplicationEngine applicationEngine) {
            options = new Object[]{localize("level.yes"), localize("level.no")};
            this.applicationEngine = applicationEngine;
        }
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            final String result = currentLevel.evaluate(applicationEngine
                    .getAtoms());
            if (result == null) {
                if (applicationEngine.getLevelManager()
                        .isCurrentLevelLastLevel()) {
                    showLastLevelClearedMessage();
                } else {
                    showLevelClearedMessage();
                }
            } else {
                showErrorMessage(result);
            }
        }

        private void showErrorMessage(final String result) {
            JOptionPane.showMessageDialog(CurrentLevelPanel.this,
                    localize("level.error") + result,
                    localize("level.error.title"), JOptionPane.ERROR_MESSAGE);
        }

        private void showLevelClearedMessage() {
            final int n = JOptionPane.showOptionDialog(CurrentLevelPanel.this,
                    localize("level.success"), localize("level.success.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            if (n == JOptionPane.YES_OPTION) {
                applicationEngine.goToNextLevel();
            }
        }

        private void showLastLevelClearedMessage() {
            JOptionPane.showMessageDialog(CurrentLevelPanel.this,
                    localize("level.fullsuccess"),
                    localize("level.success.title"),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private final class HintListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            JOptionPane.showMessageDialog(CurrentLevelPanel.this,
                    currentLevel.getHint(),
                    Messages.localize("level.hint", messageSource),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private final class LevelListener implements Listener {
        private final ApplicationEngine applicationEngine;
        private final MessageSource messageSource;

        private LevelListener(final ApplicationEngine applicationEngine,
                final MessageSource messageSource) {
            this.applicationEngine = applicationEngine;
            this.messageSource = messageSource;
        }

        @Override
        public void propertyHasChanged() {
            currentLevel = applicationEngine.getLevelManager()
                    .getCurrentLevel();
            if (currentLevel == null) {
                description.setText(Messages.localize("level.description.none",
                        messageSource));
                hintButton.setEnabled(false);
                evaluateButton.setEnabled(false);
            } else {
                description.setText("<b>" + currentLevel.getTitle() + "</b>"
                        + currentLevel.getChallenge());
                hintButton.setEnabled(StringUtils.hasText(currentLevel
                        .getHint()));
                evaluateButton.setEnabled(true);
            }
        }
    }
}
