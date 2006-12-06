package uk.org.squirm3.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.EngineListenerAdapter;
import uk.org.squirm3.engine.IApplicationEngine;

public class SimulationListener extends EngineListenerAdapter {
	
	// Actions controlling the simulation
	private final Action stop, run, reset;
	// components reflecting simulation's parameters
	private /*final*/ JSlider speedSelector, atomNumberSelector, heightSelector, widthSelector;
	private /*final*/ JFormattedTextField speedTF, atomNumberTF, heightTF, widthTF;
	private final JPanel parametersPanel;
	// use to communicate
	private IApplicationEngine iApplicationEngine;

	public SimulationListener(IApplicationEngine iApplicationEngine) {
		setApplicationEngine(iApplicationEngine);
		stop = createStopAction();
		run = createRunAction();
		reset = createResetAction();
		simulationStateHasChanged();
		parametersPanel = createParametersPanel();
	}
	
	public Action getRunAction() {
		return run;
	}
	
	public Action getStopAction() {
		return stop;
	}
	
	public Action getResetAction() {
		return reset;
	}
	
	public JPanel getParametersPanel() {
		return parametersPanel;
	}
	
	private Action createRunAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				iApplicationEngine.runSimulation();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","run"}));
	    //action.putValue(Action.LONG_DESCRIPTION, "Context-Sensitive Help Text"); TODO and for the others actions too
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("play"));
	    //action.putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_A)); TODO
		return 	action;
	}
	
	private Action createStopAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				iApplicationEngine.pauseSimulation();
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","stop"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("pause"));
		return 	action;
	}
	
	private Action createResetAction() {
		final Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				iApplicationEngine.restartLevel();;
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","simulation","reset"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("reset"));
		return 	action;
	}
	
	public JPanel createParametersPanel() {
		// parameters panel
		JPanel parametersPanel = new JPanel();
		parametersPanel.setLayout(new GridBagLayout());
		parametersPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Application.localize(new String[] {"interface","parameters","title"})));
		// parameters
			// speed
		GridBagConstraints gbc = createCustomGBC(0,0);
		parametersPanel.add(new JLabel(Application.localize(new String[] {"interface","parameters","speed"})),gbc);
		gbc = createCustomGBC(1,0);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		speedSelector = new JSlider(1,300,iApplicationEngine.getSimulationSpeed());
		speedSelector.setInverted(true);
		speedSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        iApplicationEngine.setSimulationSpeed((short)source.getValue());
			    }
			}
		});
		parametersPanel.add(speedSelector,gbc);
		gbc = createCustomGBC(2,0);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		speedTF = createCustomTF(1, 300, iApplicationEngine.getSimulationSpeed());
		speedTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					iApplicationEngine.setSimulationSpeed(((Number)e.getNewValue()).shortValue());
				}
			}
		});
		parametersPanel.add(speedTF,gbc);
			// number of atoms
		gbc = createCustomGBC(0,2);
		parametersPanel.add(new JLabel(Application.localize(new String[] {"interface","parameters","number"})),gbc);
		gbc = createCustomGBC(1,2);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		atomNumberSelector = new JSlider(30,300,iApplicationEngine.getAtomsNumber());
		atomNumberSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        iApplicationEngine.setAtomsNumber((short)source.getValue());
			    }
			}
		});
		parametersPanel.add(atomNumberSelector,gbc);
		gbc = createCustomGBC(2,2);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		atomNumberTF = createCustomTF(30,300,iApplicationEngine.getAtomsNumber());
		atomNumberTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					iApplicationEngine.setAtomsNumber(((Number)e.getNewValue()).shortValue());
				}
			}
		});
		parametersPanel.add(atomNumberTF,gbc);
			// height
		gbc = createCustomGBC(0,3);
		parametersPanel.add(new JLabel(Application.localize(new String[] {"interface","parameters","height"})),gbc);
		gbc = createCustomGBC(1,3);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		heightSelector = new JSlider(50,2000,iApplicationEngine.getSimulationHeight());
		heightSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        iApplicationEngine.setSimulationSize(iApplicationEngine.getSimulationWidth(), source.getValue());
			    }
			}
		});
		parametersPanel.add(heightSelector,gbc);
		gbc = createCustomGBC(2,3);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		heightTF = createCustomTF(50,2000,iApplicationEngine.getSimulationHeight());
		heightTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					iApplicationEngine.setSimulationSize(iApplicationEngine.getSimulationWidth(),((Number)e.getNewValue()).intValue());
				}
			}
		});
		parametersPanel.add(heightTF,gbc);
			// width
		gbc = createCustomGBC(0,4);
		parametersPanel.add(new JLabel(Application.localize(new String[] {"interface","parameters","width"})),gbc);
		gbc = createCustomGBC(1,4);
		gbc.weightx = 80;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		widthSelector = new JSlider(50,2000,iApplicationEngine.getSimulationWidth());
		widthSelector.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        iApplicationEngine.setSimulationSize(source.getValue(), iApplicationEngine.getSimulationHeight());
			    }
			}
		});
		parametersPanel.add(widthSelector,gbc);
		gbc = createCustomGBC(2,4);
		gbc.weightx = 5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		widthTF = createCustomTF(50,2000,iApplicationEngine.getSimulationWidth());
		widthTF.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if ("value".equals(e.getPropertyName())) {
					iApplicationEngine.setSimulationSize(((Number)e.getNewValue()).intValue(), iApplicationEngine.getSimulationHeight());
				}
			}
		});
		parametersPanel.add(widthTF,gbc);
		return parametersPanel;
	}

	public void setApplicationEngine(IApplicationEngine iApplicationEngine) {
		this.iApplicationEngine = iApplicationEngine;
	}

	public void atomsNumberHasChanged() {
		int number = iApplicationEngine.getAtomsNumber();
		atomNumberSelector.setValue(number);
		atomNumberTF.setValue(new Integer(number));
	}

	public void simulationSizeHasChanged() {
		int width = iApplicationEngine.getSimulationWidth();
		widthTF.setValue(new Integer(width));
		widthSelector.setValue(width);
		int height = iApplicationEngine.getSimulationHeight();
		heightTF.setValue(new Integer(height));
		heightSelector.setValue(height);
	}

	public void simulationSpeedHasChanged() {
		int speed = iApplicationEngine.getSimulationSpeed();
		speedTF.setValue(new Integer(speed));
		speedSelector.setValue(speed);
	}

	public void simulationStateHasChanged() {
		boolean isRunning = iApplicationEngine.simulationIsRunning();
		boolean resetNeeded = iApplicationEngine.simulationNeedReset();
		stop.setEnabled(!resetNeeded && isRunning);
		run.setEnabled(!resetNeeded && !isRunning);
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
