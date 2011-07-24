package uk.org.squirm3.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.springframework.context.MessageSource;

import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.action.ResetAction;
import uk.org.squirm3.ui.action.RunAction;
import uk.org.squirm3.ui.action.StopAction;
import uk.org.squirm3.ui.view.AtomsView;

public class SimulationControlPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final MessageSource messageSource;

    public SimulationControlPanel(final StopAction stopAction,
            final RunAction runAction, final ResetAction resetAction,
            final AtomsView atomsView, final ImageIcon parameterIcon,
            final SpeedPanel speedPanel, final MessageSource messageSource) {
        this.messageSource = messageSource;

        setLayout(new FlowLayout(FlowLayout.LEFT, 1, 2));
        setBackground(GUI.BACKGROUND);
        add(GUI.createIconButton(stopAction));
        add(GUI.createIconButton(runAction));
        add(GUI.createIconButton(resetAction));
        add(GUI.createIconButton(createParametersAction(speedPanel,
                atomsView.getControlsPanel(), parameterIcon)));
    }

    private Action createParametersAction(final JPanel p1, final JPanel p2,
            final ImageIcon parameterIcon) {
        final JPanel message = new JPanel();
        message.setLayout(new BoxLayout(message, BoxLayout.PAGE_AXIS));
        message.add(p1);
        message.add(p2);
        final Action action = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                JOptionPane.showMessageDialog(null, message, Messages.localize(
                        "application.parameters", messageSource),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                Messages.localize("application.parameters", messageSource));
        action.putValue(Action.SMALL_ICON, parameterIcon);
        return action;
    }

}
