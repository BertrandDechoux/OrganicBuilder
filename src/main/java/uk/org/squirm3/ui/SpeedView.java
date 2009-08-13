package uk.org.squirm3.ui;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.ISpeedListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

/**
 * ${my.copyright}
 */

public class SpeedView implements IView, ISpeedListener {

    // components reflecting simulation's parameters
    private final JSlider speedSelector;
    private final JFormattedTextField speedTF;
    private final JPanel panel;
    // use to communicate
    private ApplicationEngine applicationEngine;

    public SpeedView(ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        // speed
        GridBagConstraints gbc = createCustomGBC(0, 0);
        panel.add(new JLabel(Application.localize("parameters.speed")), gbc);
        gbc = createCustomGBC(1, 0);
        gbc.weightx = 80;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        speedSelector = new JSlider(1, 100, applicationEngine.getSimulationSpeed());
        speedSelector.setInverted(true);
        speedSelector.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    SpeedView.this.applicationEngine.setSimulationSpeed((short) source.getValue());
                }
            }
        });
        panel.add(speedSelector, gbc);
        gbc = createCustomGBC(2, 0);
        gbc.weightx = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        speedTF = createCustomTF(1, 100, applicationEngine.getSimulationSpeed());
        speedTF.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if ("value".equals(e.getPropertyName())) {
                    SpeedView.this.applicationEngine.setSimulationSpeed(((Number) e.getNewValue()).shortValue());
                }
            }
        });

        panel.add(speedTF, gbc);
        applicationEngine.getEngineDispatcher().addSpeedListener(this);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void simulationSpeedHasChanged() {
        int speed = applicationEngine.getSimulationSpeed();
        speedTF.setValue(new Integer(speed));
        speedSelector.setValue(speed);
    }

    private JFormattedTextField createCustomTF(int min, int max, int now) {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setMinimum(new Integer(min));
        formatter.setMaximum(new Integer(max));
        JFormattedTextField TF = new JFormattedTextField(formatter);
        TF.setValue(new Integer(now));
        TF.setColumns(5);
        return TF;
    }

    private GridBagConstraints createCustomGBC(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }
}
