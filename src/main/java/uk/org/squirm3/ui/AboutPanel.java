package uk.org.squirm3.ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import uk.org.squirm3.ui.action.AboutAction;

public class AboutPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public AboutPanel(final AboutAction aboutAction) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 2));
        setBackground(GUI.BACKGROUND);
        add(GUI.createIconButton(aboutAction));
    }

}
