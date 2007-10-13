package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
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
import uk.org.squirm3.data.Reaction;
import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.ILevelListener;


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

public class CurrentLevelView implements IView, ILevelListener {
	private IApplicationEngine iApplicationEngine;
	private JEditorPane description;
	private JButton hintButton, evaluateButton;
	private Level currentLevel;
	
	private final JPanel currentLevelPanel;

	public CurrentLevelView(IApplicationEngine iApplicationEngine) {
		currentLevelPanel = createCurrentLevelPanel();
		this.iApplicationEngine = iApplicationEngine;
		levelHasChanged();
		iApplicationEngine.getEngineDispatcher().addLevelListener(this);	
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
					//TODO keep always the same object
					// TODO store the url into a configuration file
					ILogger logger = new NetLogger("http://organicbuilder.sourceforge.net/log-solution");
					// Old one http://www.sq3.org.uk/Evolution/Squirm3/OrganicBuilder/logger.pl
					logger.writeSolution(currentLevel.getId(), iApplicationEngine.getReactions());
					
					result = Application.localize(new String[] {"interface","level","fullsuccess"});
					if(iApplicationEngine.getCurrentLevel().getId()+1>iApplicationEngine.getLevels().size()-1) {
						JOptionPane.showMessageDialog(currentLevelPanel, result,
							Application.localize(new String[] {"interface","level","success","title"}),
							JOptionPane.INFORMATION_MESSAGE);
					} else {
						Object[] options = {Application.localize(new String[] {"interface","level","yes"}),
								Application.localize(new String[] {"interface","level","no"})};
						int n = JOptionPane.showOptionDialog(currentLevelPanel,
						    result, Application.localize(new String[] {"interface","level","success","title"}),
						    JOptionPane.YES_NO_OPTION,
						    JOptionPane.INFORMATION_MESSAGE,
						    null,     
						    options,  
						    options[0]);
						if(n==JOptionPane.YES_OPTION) iApplicationEngine.goToNextLevel();
					}
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
}

// interface ILogger : write the reactions that solved a challenge
interface ILogger {
	public void writeSolution(int levelNumber, Collection reactions);
}

// NetLogger : an implementation of the ILogger interface
class NetLogger implements ILogger {
	
	private final String url;
	
	public NetLogger(String url) {
		this.url = url;
	}

	public void writeSolution(int levelNumber, Collection reactions) {
		 if(levelNumber>0) // do you want to log the solution or not?
		  {
			 try {
				  URL url = new URL(this.url);
				  URLConnection connection = url.openConnection();
				  connection.setDoOutput(true);
				  
				  PrintWriter out = new PrintWriter(connection.getOutputStream());
				  // chalenge number
				  out.println(String.valueOf(levelNumber));
				  // number of reactions
				  out.println(String.valueOf(reactions.size()));
				  //TODO size is the number of reactions or the number of possibles reactions ? (size!=length)
				  Iterator it = reactions.iterator();
				  while(it.hasNext()) out.println(((Reaction)it.next()).toString());
				  out.close();
				  //to read (debug)
				  BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				  String inputLine;
				  while ((inputLine = in.readLine()) != null) System.out.println(inputLine);
				  in.close();
			  } 
			  // it doesn't matter too much if we couldn't connect, just skip it
			 // catch all exceptions : MalformedURLException, IOException and others
			  catch (Exception error) { }
		  }
	}

}

