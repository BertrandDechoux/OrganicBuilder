package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.engine.EngineListenerAdapter;
import uk.org.squirm3.engine.IApplicationEngine;


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
along with Foobar; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class CurrentLevelListener extends EngineListenerAdapter {
	private IApplicationEngine iApplicationEngine;
	private JEditorPane description;
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
		description = new JEditorPane();
		description.setContentType("text/html");
		description.setEditable(false);
		JScrollPane p = new JScrollPane(description);
		p.setMinimumSize(new Dimension(50, 100));
		jPanel.add(p, BorderLayout.CENTER);
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
					Resource.logSolution(currentLevel.getId(),iApplicationEngine.getReactions());
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
			description.setText("<b>"+currentLevel.getTitle()+"</b>"+currentLevel.getChallenge());
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
