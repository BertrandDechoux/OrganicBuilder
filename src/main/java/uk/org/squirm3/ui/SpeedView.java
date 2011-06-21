package uk.org.squirm3.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class SpeedView extends AView {

    // components reflecting simulation's parameters
    private final JSlider speedSelector;
    private final JFormattedTextField speedTF;
    private final JPanel panel;

    public SpeedView(final ApplicationEngine applicationEngine) {
        super(applicationEngine);
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        // speed
        GridBagConstraints gbc = createCustomGBC(0, 0);
        panel.add(new JLabel(GUI.localize("parameters.speed")), gbc);
        gbc = createCustomGBC(1, 0);
        gbc.weightx = 80;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        speedSelector = new JSlider(1, 100, getApplicationEngine()
                .getSimulationSpeed());
        speedSelector.setInverted(true);
        speedSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    SpeedView.this.getApplicationEngine().setSimulationSpeed(
                            (short) source.getValue());
                }
            }
        });
        panel.add(speedSelector, gbc);
        gbc = createCustomGBC(2, 0);
        gbc.weightx = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        speedTF = createCustomTF(1, 100, getApplicationEngine()
                .getSimulationSpeed());
        speedTF.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent e) {
                if ("value".equals(e.getPropertyName())) {
                    SpeedView.this.getApplicationEngine().setSimulationSpeed(
                            ((Number) e.getNewValue()).shortValue());
                }
            }
        });

        panel.add(speedTF, gbc);

        getApplicationEngine().getEventDispatcher().addListener(
                new IListener() {
                    @Override
                    public void propertyHasChanged() {
                        final int speed = getApplicationEngine()
                                .getSimulationSpeed();
                        speedTF.setValue(new Integer(speed));
                        speedSelector.setValue(speed);
                    }
                }, EventDispatcher.Event.SPEED);
    }

    public JPanel getPanel() {
        return panel;
    }

    private JFormattedTextField createCustomTF(final int min, final int max,
            final int now) {
        final NumberFormatter formatter = new NumberFormatter(
                NumberFormat.getIntegerInstance());
        formatter.setMinimum(new Integer(min));
        formatter.setMaximum(new Integer(max));
        final JFormattedTextField TF = new JFormattedTextField(formatter);
        TF.setValue(new Integer(now));
        TF.setColumns(5);
        return TF;
    }

    private GridBagConstraints createCustomGBC(final int x, final int y) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }
}
