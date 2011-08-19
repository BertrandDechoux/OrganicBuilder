package uk.org.squirm3.ui.toolbar.actions;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.ui.toolbar.SpeedPanel;

// TODO redesign // rename : it is only about speed
public class ParametersAction extends SquirmAction {
    private static final long serialVersionUID = 1L;

    private final JPanel message;

    public ParametersAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon parametersIcon,
            final SpeedPanel speedPanel) {
        super(messageSource, applicationEngine, "application.parameters",
                parametersIcon);

        message = speedPanel;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        JOptionPane.showMessageDialog(null, message,
                localize("application.parameters"),
                JOptionPane.INFORMATION_MESSAGE);
    }

}
