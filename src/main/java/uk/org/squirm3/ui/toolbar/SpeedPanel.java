package uk.org.squirm3.ui.toolbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.SwingUtils;

public class SpeedPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public SpeedPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource) {

        final JSlider speedSelector = createSpeedSelector(applicationEngine);
        final JFormattedTextField speedTF = createSpeedTextField(applicationEngine);
        applicationEngine.getEventDispatcher().addListener(new IListener() {
            @Override
            public void propertyHasChanged() {
                final int speed = applicationEngine.getSimulationSpeed();
                speedTF.setValue(new Integer(speed));
                speedSelector.setValue(speed);
            }
        }, EventDispatcher.Event.SPEED);

        setLayout(new GridBagLayout());
        add(new JLabel(Messages.localize("parameters.speed", messageSource)),
                SwingUtils.createCustomGBC(0, 0));
        add(speedSelector, SwingUtils.createCustomGBC(1, 0, 80,
                GridBagConstraints.HORIZONTAL));
        add(speedTF, SwingUtils.createCustomGBC(2, 0, 5,
                GridBagConstraints.HORIZONTAL));

    }

    private JFormattedTextField createSpeedTextField(
            final ApplicationEngine applicationEngine) {
        final JFormattedTextField speedTF = SwingUtils.createIntegerTextField(1, 100,
                applicationEngine.getSimulationSpeed(), 5);
        speedTF.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent e) {
                if ("value".equals(e.getPropertyName())) {
                    applicationEngine.setSimulationSpeed(((Number) e
                            .getNewValue()).shortValue());
                }
            }
        });
        return speedTF;
    }

    private JSlider createSpeedSelector(
            final ApplicationEngine applicationEngine) {
        final JSlider speedSelector = new JSlider(1, 100,
                applicationEngine.getSimulationSpeed());
        speedSelector.setInverted(true);
        speedSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    applicationEngine.setSimulationSpeed((short) source
                            .getValue());
                }
            }
        });
        return speedSelector;
    }
}
