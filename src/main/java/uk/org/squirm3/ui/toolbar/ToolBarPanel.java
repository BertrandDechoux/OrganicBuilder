package uk.org.squirm3.ui.toolbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import uk.org.squirm3.ui.toolbar.navigation.LevelPicker;
import uk.org.squirm3.ui.toolbar.simulation.SpeedPanel;

public class ToolBarPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(255, 255, 225);

    public ToolBarPanel(
            final Action runSimulationAction,
            final Action stopSimulationAction,
            final Action resetSimulationAction,
            final SpeedPanel speedPanel,
            final Action firstLevelAction,
            final Action previousLevelAction,
            final LevelPicker levelPicker,
            final Action nextLevelAction,
            final Action lastLevelAction,
            final Action aboutAction) {

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(BACKGROUND);

        addActions(stopSimulationAction, runSimulationAction,
                resetSimulationAction);

        addGlueSeparartor(1);

        speedPanel.setBackground(BACKGROUND);
        add(speedPanel);

        addGlueSeparartor(2);

        addActions(firstLevelAction, previousLevelAction);
        levelPicker.setMaximumSize(new Dimension(150, 80));
        add(levelPicker);
        addActions(nextLevelAction, lastLevelAction);

        addGlueSeparartor(6);

        addActions(aboutAction);
    }

    /**
     * Add all provided actions as standard {@link JButton}s.
     */
    private void addActions(final Action... actions) {
        for (final Action action : actions) {
            add(createIconButton(action));
        }
    }

    /**
     * Add a glue component having the provided strength. The higher the
     * strength, the higher free space the glue will take with regards to other
     * components.
     */
    private void addGlueSeparartor(final int strenght) {
        for (int i = 0; i < strenght; i++) {
            add(Box.createHorizontalGlue());
        }
    }

    /**
     * @return a standard {@link JButton}n for this toolbar created from the
     *         provided {@link Action}
     */
    public static JButton createIconButton(final Action action) {
        final JButton button = new JButton(action);
        button.setMargin(new Insets(-3, -3, -3, -3));
        button.setBorderPainted(false);
        button.setBackground(BACKGROUND);
        return button;
    }

}
