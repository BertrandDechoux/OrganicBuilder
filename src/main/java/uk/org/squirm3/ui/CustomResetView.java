package uk.org.squirm3.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.model.Configuration;

public class CustomResetView extends AView {

    // components reflecting simulation's parameters
    private/* final */JSlider atomNumberSelector, heightSelector,
            widthSelector;
    private/* final */JFormattedTextField atomNumberTF, heightTF, widthTF;
    private final JPanel panel;

    public CustomResetView(final ApplicationEngine applicationEngine) {
        super(applicationEngine);
        panel = createParametersPanel();

        final IListener levelListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                final Configuration configuration = getApplicationEngine()
                        .getLevelManager().getCurrentLevel().getConfiguration();
                updateNumberOfAtoms(configuration.getNumberOfAtoms());
                updateWidth((int) configuration.getWidth());
                updateHeight((int) configuration.getHeight());
            }
        };
        levelListener.propertyHasChanged();
        applicationEngine.getEventDispatcher().addListener(levelListener,
                EventDispatcher.Event.LEVEL);
        applicationEngine.getEventDispatcher().addListener(levelListener,
                EventDispatcher.Event.CONFIGURATION);
    }

    public JPanel getPanel() {
        return panel;
    }

    public JPanel createParametersPanel() {
        // parameters panel
        final JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridBagLayout());
        parametersPanel.setBorder(BorderFactory
                .createEtchedBorder(EtchedBorder.LOWERED));
        // parameters
        // number of atoms
        GridBagConstraints gbc = createCustomGBC(0, 0);
        parametersPanel.add(new JLabel(GUI.localize("parameters.number")), gbc);
        gbc = createCustomGBC(1, 0);
        gbc.weightx = 80;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        atomNumberSelector = new JSlider(30, 300, 30);
        atomNumberSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    updateNumberOfAtoms(source.getValue());
                }
            }
        });
        parametersPanel.add(atomNumberSelector, gbc);
        gbc = createCustomGBC(2, 0);
        gbc.weightx = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        atomNumberTF = createCustomTF(30, 300, 30);
        atomNumberTF.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent e) {
                if ("value".equals(e.getPropertyName())) {
                    updateNumberOfAtoms(((Number) e.getNewValue()).intValue());
                }
            }
        });
        parametersPanel.add(atomNumberTF, gbc);
        // height
        gbc = createCustomGBC(0, 1);
        parametersPanel.add(new JLabel(GUI.localize("parameters.height")), gbc);
        gbc = createCustomGBC(1, 1);
        gbc.weightx = 80;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        heightSelector = new JSlider(50, 2000, 50);
        heightSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    updateHeight(source.getValue());
                }
            }
        });
        parametersPanel.add(heightSelector, gbc);
        gbc = createCustomGBC(2, 1);
        gbc.weightx = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        heightTF = createCustomTF(50, 2000, 50);
        heightTF.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent e) {
                if ("value".equals(e.getPropertyName())) {
                    updateHeight(((Number) e.getNewValue()).intValue());
                }
            }
        });
        parametersPanel.add(heightTF, gbc);
        // width
        gbc = createCustomGBC(0, 2);
        parametersPanel.add(new JLabel(GUI.localize("parameters.width")), gbc);
        gbc = createCustomGBC(1, 2);
        gbc.weightx = 80;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        widthSelector = new JSlider(50, 2000, 50);
        widthSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                final JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    updateWidth(source.getValue());
                }
            }
        });
        parametersPanel.add(widthSelector, gbc);
        gbc = createCustomGBC(2, 2);
        gbc.weightx = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        widthTF = createCustomTF(50, 2000, 50);
        widthTF.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent e) {
                if ("value".equals(e.getPropertyName())) {
                    updateWidth(((Number) e.getNewValue()).intValue());
                }
            }
        });
        parametersPanel.add(widthTF, gbc);

        gbc = createCustomGBC(2, 3);
        final JButton resetButton = new JButton(
                GUI.localize("simulation.reset"));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                final Configuration configuration = new Configuration(
                        atomNumberSelector.getValue(),
                        widthSelector.getValue(), heightSelector.getValue());
                getApplicationEngine().restartLevel(configuration);
            }
        });
        parametersPanel.add(resetButton, gbc);
        return parametersPanel;
    }

    public void updateNumberOfAtoms(final int numberOfAtoms) {
        atomNumberSelector.setValue(numberOfAtoms);
        atomNumberTF.setValue(new Integer(numberOfAtoms));
    }

    public void updateWidth(final int width) {
        widthTF.setValue(new Integer(width));
        widthSelector.setValue(width);
    }

    public void updateHeight(final int height) {
        heightTF.setValue(new Integer(height));
        heightSelector.setValue(height);
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
