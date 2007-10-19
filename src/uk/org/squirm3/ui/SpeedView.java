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

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.ISpeedListener;


/**  
Copyright 2007 Tim J. Hutton, Ralph Hartley, Bertrand Dechoux

This file is part of Organic Builder.

Organic Builder is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Organic Builder is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Organic Builder; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class SpeedView implements IView, ISpeedListener {

	// components reflecting simulation's parameters
	private final JSlider speedSelector;
	private final JFormattedTextField speedTF;
	private final JPanel panel;
	// use to communicate
	private IApplicationEngine iApplicationEngine;

	public SpeedView(IApplicationEngine iApplicationEngine) {
		this.iApplicationEngine = iApplicationEngine;
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
			// speed
		GridBagConstraints gbc = createCustomGBC(0,0);
		panel.add(new JLabel(Application.localize(new String[] {"interface","parameters","speed"})),gbc);
		gbc = createCustomGBC(1,0);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		speedSelector = new JSlider(1,100,iApplicationEngine.getSimulationSpeed());
		speedSelector.setInverted(true);
		speedSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        SpeedView.this.iApplicationEngine.setSimulationSpeed((short)source.getValue());
			    }
			}
		});
		panel.add(speedSelector,gbc);
		gbc = createCustomGBC(2,0);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		speedTF = createCustomTF(1, 100, iApplicationEngine.getSimulationSpeed());
		speedTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					SpeedView.this.iApplicationEngine.setSimulationSpeed(((Number)e.getNewValue()).shortValue());
				}
			}
		});
		
		panel.add(speedTF,gbc);
		iApplicationEngine.getEngineDispatcher().addSpeedListener(this);
	}
	
	public JPanel getPanel() {
		return panel;
	}

	public void simulationSpeedHasChanged() {
		int speed = iApplicationEngine.getSimulationSpeed();
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
