package uk.org.squirm3.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class ToolBarPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public ToolBarPanel(final SimulationControlPanel simulationControlPanel,
            final LevelsControlPanel levelsControlPanel,
            final AboutPanel aboutPanel) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(GUI.BACKGROUND);
        add(simulationControlPanel);
        add(Box.createHorizontalGlue());
        add(levelsControlPanel);
        add(Box.createHorizontalGlue());
        add(aboutPanel);

    }

}
