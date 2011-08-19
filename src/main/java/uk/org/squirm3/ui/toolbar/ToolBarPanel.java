package uk.org.squirm3.ui.toolbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import uk.org.squirm3.ui.toolbar.actions.AboutAction;
import uk.org.squirm3.ui.toolbar.actions.FirstLevelAction;
import uk.org.squirm3.ui.toolbar.actions.LastLevelAction;
import uk.org.squirm3.ui.toolbar.actions.NextLevelAction;
import uk.org.squirm3.ui.toolbar.actions.ParametersAction;
import uk.org.squirm3.ui.toolbar.actions.PreviousLevelAction;
import uk.org.squirm3.ui.toolbar.actions.ResetSimulationAction;
import uk.org.squirm3.ui.toolbar.actions.RunSimulationAction;
import uk.org.squirm3.ui.toolbar.actions.StopSimulationAction;


public class ToolBarPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(255, 255, 225);

    public ToolBarPanel(final StopSimulationAction stopSimulationAction, final RunSimulationAction runSimulationAction,
            final ResetSimulationAction resetSimulationAction,
            final ParametersAction parametersAction,
            final FirstLevelAction firstLevelAction, final PreviousLevelAction previousLevelAction,
            final LevelPicker levelPicker, final NextLevelAction nextLevelAction,
            final LastLevelAction lastLevelAction, final AboutAction aboutAction) {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(ToolBarPanel.BACKGROUND);

        add(createSimulationControlPanel(stopSimulationAction, runSimulationAction, resetSimulationAction,
                parametersAction));
        add(Box.createHorizontalGlue());
        add(createLevelsControlPanel(firstLevelAction, previousLevelAction, levelPicker,
                nextLevelAction, lastLevelAction));
        add(Box.createHorizontalGlue());
        add(createAboutPanel(aboutAction));
    }

    private JPanel createSimulationControlPanel(final StopSimulationAction stopAction,
            final RunSimulationAction runAction, final ResetSimulationAction resetAction,
            final ParametersAction parametersAction) {
        final JPanel simulationControlPanel = new JPanel();
        simulationControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 2));
        simulationControlPanel.setBackground(ToolBarPanel.BACKGROUND);
        simulationControlPanel.add(ToolBarPanel.createIconButton(stopAction));
        simulationControlPanel.add(ToolBarPanel.createIconButton(runAction));
        simulationControlPanel.add(ToolBarPanel.createIconButton(resetAction));
        simulationControlPanel.add(ToolBarPanel.createIconButton(parametersAction));
        return simulationControlPanel;
    }

    private JPanel createLevelsControlPanel(final FirstLevelAction introAction,
            final PreviousLevelAction previousAction, final LevelPicker levelPicker,
            final NextLevelAction nextAction, final LastLevelAction lastAction) {
        final JPanel levelsControlPanel = new JPanel();
        levelsControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
        levelsControlPanel.setBackground(ToolBarPanel.BACKGROUND);
        levelsControlPanel.add(ToolBarPanel.createIconButton(introAction));
        levelsControlPanel.add(ToolBarPanel.createIconButton(previousAction));
        levelPicker.setMaximumSize(new Dimension(150, 80));
        levelsControlPanel.add(levelPicker);
        levelsControlPanel.add(ToolBarPanel.createIconButton(nextAction));
        levelsControlPanel.add(ToolBarPanel.createIconButton(lastAction));
        return levelsControlPanel;
    }

    private JPanel createAboutPanel(final AboutAction aboutAction) {
        final JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 2));
        aboutPanel.setBackground(ToolBarPanel.BACKGROUND);
        aboutPanel.add(ToolBarPanel.createIconButton(aboutAction));
        return aboutPanel;
    }

    public static JButton createIconButton(final Action action) {
        final JButton button = new JButton(action);
        button.setMargin(new Insets(-3, -3, -3, -3));
        button.setBorderPainted(false);
        button.setBackground(ToolBarPanel.BACKGROUND);
        return button;
    }

}
