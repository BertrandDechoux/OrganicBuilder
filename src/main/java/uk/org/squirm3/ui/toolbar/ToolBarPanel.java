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

import org.springframework.beans.factory.annotation.Qualifier;

import uk.org.squirm3.ui.toolbar.navigation.LevelPicker;
import uk.org.squirm3.ui.toolbar.simulation.SpeedPanel;

public class ToolBarPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(255, 255, 225);

    public ToolBarPanel(
            @Qualifier("runSimulationAction") final Action runSimulationAction,
            @Qualifier("stopSimulationAction") final Action stopSimulationAction,
            @Qualifier("resetSimulationAction") final Action resetSimulationAction,
            final SpeedPanel speedPanel,
            @Qualifier("firstLevelAction") final Action firstLevelAction,
            @Qualifier("previousLevelAction") final Action previousLevelAction,
            final LevelPicker levelPicker,
            @Qualifier("nextLevelAction") final Action nextLevelAction,
            @Qualifier("lastLevelAction") final Action lastLevelAction,
            @Qualifier("aboutAction") final Action aboutAction) {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(ToolBarPanel.BACKGROUND);

        add(createSimulationControlPanel(stopSimulationAction,
                runSimulationAction, resetSimulationAction));
        add(Box.createHorizontalGlue());
        add(createSpeedPanel(speedPanel));
        add(Box.createHorizontalGlue());
        add(createLevelsControlPanel(firstLevelAction, previousLevelAction,
                levelPicker, nextLevelAction, lastLevelAction));
        add(Box.createHorizontalGlue());
        add(createAboutPanel(aboutAction));
    }

    private JPanel createSimulationControlPanel(final Action stopAction,
            final Action runAction, final Action resetAction) {
        final JPanel simulationControlPanel = new JPanel();
        simulationControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 2));
        simulationControlPanel.setBackground(BACKGROUND);
        simulationControlPanel.add(createIconButton(stopAction));
        simulationControlPanel.add(createIconButton(runAction));
        simulationControlPanel.add(createIconButton(resetAction));
        return simulationControlPanel;
    }

    private JPanel createSpeedPanel(final JPanel speedPanel) {
        final JPanel speedLayoutPanel = new JPanel();
        speedLayoutPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
        speedLayoutPanel.setBackground(BACKGROUND);
        speedPanel.setBackground(BACKGROUND);
        speedLayoutPanel.add(speedPanel);
        return speedLayoutPanel;
    }

    private JPanel createLevelsControlPanel(final Action introAction,
            final Action previousAction, final LevelPicker levelPicker,
            final Action nextAction, final Action lastAction) {
        final JPanel levelsControlPanel = new JPanel();
        levelsControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
        levelsControlPanel.setBackground(BACKGROUND);
        levelsControlPanel.add(createIconButton(introAction));
        levelsControlPanel.add(createIconButton(previousAction));
        levelPicker.setMaximumSize(new Dimension(150, 80));
        levelsControlPanel.add(levelPicker);
        levelsControlPanel.add(createIconButton(nextAction));
        levelsControlPanel.add(createIconButton(lastAction));
        return levelsControlPanel;
    }

    private JPanel createAboutPanel(final Action aboutAction) {
        final JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 2));
        aboutPanel.setBackground(BACKGROUND);
        aboutPanel.add(createIconButton(aboutAction));
        return aboutPanel;
    }

    public static JButton createIconButton(final Action action) {
        final JButton button = new JButton(action);
        button.setMargin(new Insets(-3, -3, -3, -3));
        button.setBorderPainted(false);
        button.setBackground(BACKGROUND);
        return button;
    }

}
