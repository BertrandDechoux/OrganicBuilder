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

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.ILevelListener;


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

public class CustomResetView implements IView, ILevelListener {

	// components reflecting simulation's parameters
	private /*final*/ JSlider atomNumberSelector, heightSelector, widthSelector;
	private /*final*/ JFormattedTextField atomNumberTF, heightTF, widthTF;
	private final JPanel panel;
	// use to communicate
	private ApplicationEngine applicationEngine;

	public CustomResetView(ApplicationEngine applicationEngine) {
		this.applicationEngine = applicationEngine;
		panel = createParametersPanel();
		applicationEngine.getEngineDispatcher().addLevelListener(this);
		configurationHasChanged();
	}
	
	public JPanel getPanel() {
		return panel;
	}

	public JPanel createParametersPanel() {
		// parameters panel
		JPanel parametersPanel = new JPanel();
		parametersPanel.setLayout(new GridBagLayout());
		parametersPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		// parameters
			// number of atoms
		GridBagConstraints gbc = createCustomGBC(0,0);
		parametersPanel.add(new JLabel(Application.localize(new String[] {"interface","parameters","number"})),gbc);
		gbc = createCustomGBC(1,0);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		atomNumberSelector = new JSlider(30,300,30);
		atomNumberSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			    	updateNumberOfAtoms(source.getValue());
			    }
			}
		});
		parametersPanel.add(atomNumberSelector,gbc);
		gbc = createCustomGBC(2,0);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		atomNumberTF = createCustomTF(30,300,30);
		atomNumberTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					updateNumberOfAtoms(((Number)e.getNewValue()).intValue());
				}
			}
		});
		parametersPanel.add(atomNumberTF,gbc);
			// height
		gbc = createCustomGBC(0,1);
		parametersPanel.add(new JLabel(Application.localize(new String[] {"interface","parameters","height"})),gbc);
		gbc = createCustomGBC(1,1);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		heightSelector = new JSlider(50,2000,50);
		heightSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        updateHeight(source.getValue());
			    }
			}
		});
		parametersPanel.add(heightSelector,gbc);
		gbc = createCustomGBC(2,1);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		heightTF = createCustomTF(50,2000,50);
		heightTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					updateHeight(((Number)e.getNewValue()).intValue());
				}
			}
		});
		parametersPanel.add(heightTF,gbc);
			// width
		gbc = createCustomGBC(0,2);
		parametersPanel.add(new JLabel(Application.localize(new String[] {"interface","parameters","width"})),gbc);
		gbc = createCustomGBC(1,2);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		widthSelector = new JSlider(50,2000,50);
		widthSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        updateWidth(source.getValue());
			    }
			}
		});
		parametersPanel.add(widthSelector,gbc);
		gbc = createCustomGBC(2,2);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		widthTF = createCustomTF(50,2000,50);
		widthTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					updateWidth(((Number)e.getNewValue()).intValue());
				}
			}
		});
		parametersPanel.add(widthTF,gbc);
		
		gbc = createCustomGBC(2,3);
		JButton resetButton = new JButton(Application.localize(new String[] {"interface","simulation","reset"}));
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Configuration configuration = new Configuration(atomNumberSelector.getValue(),
						Level.TYPES, widthSelector.getValue(), heightSelector.getValue());
				applicationEngine.restartLevel(configuration);
			}});
		parametersPanel.add(resetButton,gbc);
		return parametersPanel;
	}

	public void updateNumberOfAtoms(int numberOfAtoms) {
		atomNumberSelector.setValue(numberOfAtoms);
		atomNumberTF.setValue(new Integer(numberOfAtoms));
	}

	public void updateWidth(int width) {
		widthTF.setValue(new Integer(width));
		widthSelector.setValue(width);
	}
	
	public void updateHeight(int height) {
		heightTF.setValue(new Integer(height));
		heightSelector.setValue(height);
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

	public void levelHasChanged() {
		configurationHasChanged();
	}

	public void configurationHasChanged() {
		Configuration configuration = applicationEngine.getLevelManager().getCurrentLevel().getConfiguration();
		updateNumberOfAtoms(configuration.getNumberOfAtoms());
		updateWidth((int)configuration.getWidth());
		updateHeight((int)configuration.getHeight());
	}

}
