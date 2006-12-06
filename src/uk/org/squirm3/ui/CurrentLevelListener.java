package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.engine.EngineListenerAdapter;
import uk.org.squirm3.engine.IApplicationEngine;

public class CurrentLevelListener extends EngineListenerAdapter {
	private IApplicationEngine iApplicationEngine;
	private JTextArea description;
	private JButton hintButton, evaluateButton;
	private Level currentLevel;
	
	private final JPanel currentLevelPanel;

	public CurrentLevelListener(IApplicationEngine iApplicationEngine) {
		currentLevelPanel = createCurrentLevelPanel();
		setApplicationEngine(iApplicationEngine);
	}
	
	private JPanel createCurrentLevelPanel() {
		final JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		description = new JTextArea();
		description.setRows(10);
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setEditable(false);
		jPanel.add(new JScrollPane(description), BorderLayout.CENTER);
		jPanel.add(createButtonsPanel(),BorderLayout.SOUTH);
		return jPanel;
	}
	
	public JPanel getCurrentLevelPanel() {
		return currentLevelPanel;
	}
	
	private JPanel createButtonsPanel() {
		final JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.LINE_AXIS));
		hintButton = new JButton(Application.localize(new String[] {"interface","level","hint"}));
		hintButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(currentLevelPanel,currentLevel.getHint(),Application.localize(new String[] {"interface","level","hint"}),JOptionPane.INFORMATION_MESSAGE);
			}	
		});
		jPanel.add(hintButton);
		jPanel.add(Box.createHorizontalGlue());
		evaluateButton = new JButton(Application.localize(new String[] {"interface","level","evaluate"}));
		evaluateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Collection c = iApplicationEngine.getAtoms();
				Iterator it = c.iterator();
				Atom[] atoms = new Atom[c.size()];
				int i = 0;
				while(it.hasNext()) {
					atoms[i] = (Atom)it.next();
					i++;
				}
				String result = currentLevel.evaluate(atoms);
				boolean success = true;
				if(result==null){
					result = Application.localize(new String[] {"interface","level","success"});
				}
				else {
						result = Application.localize(new String[] {"interface","level","error"})+result;
						success = false;
				}
				if(success) {
					JOptionPane.showMessageDialog(currentLevelPanel, result,
							Application.localize(new String[] {"interface","level","success","title"}), JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(currentLevelPanel, result,
							Application.localize(new String[] {"interface","level","error","title"}), JOptionPane.ERROR_MESSAGE);
				}
			}	
		});
		jPanel.add(evaluateButton);
		return jPanel;
	}
	
	public void levelHasChanged() {
		currentLevel = iApplicationEngine.getCurrentLevel();
		if(currentLevel==null) {
			description.setText(Application.localize(new String[] {"interface","level","description","none"}));
			hintButton.setEnabled(false);
			evaluateButton.setEnabled(false);
		} else {
			description.setText(currentLevel.getChallenge());
			if(currentLevel.getHint()==null || currentLevel.getHint().equals("")) {
				hintButton.setEnabled(false);
			} else {
				hintButton.setEnabled(true);
			}
			evaluateButton.setEnabled(true);
		}	
	}

	public void setApplicationEngine(IApplicationEngine iApplicationEngine) {
		this.iApplicationEngine = iApplicationEngine;
		levelHasChanged();
	}
}
