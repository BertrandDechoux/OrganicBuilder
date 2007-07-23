package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Reaction;
import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.IReactionListener;


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

public class ReactionListView implements IView, IReactionListener {
	// commucation with the engine
	private IApplicationEngine iApplicationEngine;
	// list mode
	private JButton editButton, deleteButton, clearButton;
	private JList reactionsList;
	private JPanel listButtonsPanel;
	// edit mode
	private JButton updateButton;
	private JTextArea textArea;
	// main components
	private JPanel buttonParentPanel;
	private JScrollPane scrollPanel;
	private TitledBorder border;
	private final JPanel listPanel;
	
	boolean isBeingEdited = false;

	public ReactionListView(IApplicationEngine iApplicationEngine) {
		this.iApplicationEngine = iApplicationEngine;
		listPanel = createListPanel();
		textArea = new JTextArea();
		iApplicationEngine.addReactionListener(this);
		reactionsHaveChanged();
		updateDeleteButton();
	}
	
	public JPanel getListPanel() {
		return listPanel;
	}
	
	private JPanel createListPanel() {
		final JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Application.localize(new String[] {"interface","reactions","current"}));
		jPanel.setBorder(border);
		listButtonsPanel = new JPanel();
		listButtonsPanel.setLayout(new GridLayout(4,1));
		
		editButton = new JButton(Application.localize(new String[] {"interface","reactions","edit"}));
		editButton.addActionListener((ActionListener)EventHandler.create(ActionListener.class, ReactionListView.this, "editReactions"));
		listButtonsPanel.add(editButton);
		
		updateButton = new JButton(Application.localize(new String[] {"interface","reactions","update"}));
		updateButton.addActionListener((ActionListener)EventHandler.create(ActionListener.class, ReactionListView.this, "updateReactions"));
		
		deleteButton = new JButton(Application.localize(new String[] {"interface","reactions","delete"}));
		deleteButton.addActionListener((ActionListener)EventHandler.create(ActionListener.class, ReactionListView.this, "deleteSelectedReactions"));
		listButtonsPanel.add(deleteButton);
		
		clearButton = new JButton(Application.localize(new String[] {"interface","reactions","clear"}));
		clearButton.addActionListener((ActionListener)EventHandler.create(ActionListener.class, iApplicationEngine, "clearReactions"));
		listButtonsPanel.add(clearButton);
		
		buttonParentPanel = new JPanel();
		buttonParentPanel.setLayout(new BorderLayout());
		buttonParentPanel.add(listButtonsPanel, BorderLayout.NORTH);
		jPanel.add(buttonParentPanel, BorderLayout.EAST);
		
		reactionsList = new JList();
		reactionsList.getSelectionModel().addListSelectionListener(
				(ListSelectionListener)EventHandler.create(ListSelectionListener.class, ReactionListView.this, "updateDeleteButton"));
		reactionsList.addMouseListener( new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					editReactions();
				}
			}
		});
		scrollPanel = new JScrollPane(reactionsList);
		jPanel.add(scrollPanel,BorderLayout.CENTER);
		return jPanel;
	}
	
	public void editReactions() {
		if(isBeingEdited==true) return;
		else isBeingEdited = true;
		buttonParentPanel.remove(listButtonsPanel);
		buttonParentPanel.add(updateButton, BorderLayout.NORTH);
		buttonParentPanel.updateUI();
		Object[] reactions = iApplicationEngine.getReactions().toArray();
		String content = "";
		for(int i = 0 ; i<reactions.length ; i++) {
			content += reactions[i].toString()+"\n";
		}
		textArea.setText(content);
		scrollPanel.setViewportView(textArea);
	}
	
	public void updateReactions() {
		if(isBeingEdited==false) return;
		else isBeingEdited = false;
		Vector v = new Vector();
		String result = Reaction.parse(textArea.getText(), v);
		if(result!=null) {
			JOptionPane.showMessageDialog(listPanel,result,
					Application.localize(new String[] {"interface","reactions","parsing","error"}),
					JOptionPane.ERROR_MESSAGE);
		} else {
			iApplicationEngine.setReactions(v);
			reactionsHaveChanged();
			buttonParentPanel.remove(updateButton);
			buttonParentPanel.add(listButtonsPanel, BorderLayout.NORTH);
			scrollPanel.setViewportView(reactionsList);
		}
	}
	
	public void deleteSelectedReactions() {
		Object[] reactions = reactionsList.getSelectedValues();
		Collection c = new ArrayList(reactions.length);
		for(int i = 0; i < reactions.length ; i++) {
			c.add(reactions[i]);
		}
		iApplicationEngine.removeReactions(c);
	}
	
	public void updateDeleteButton() {
		deleteButton.setEnabled(reactionsList.getSelectedValues().length!=0);
	}

	public void reactionsHaveChanged() {
		Object[] reactions = iApplicationEngine.getReactions().toArray();
		border.setTitle(Application.localize(new String[] {"interface","reactions","current"})+" ("+reactions.length+")");
		clearButton.setEnabled(reactions.length!=0);
		reactionsList.setListData(reactions);
		listPanel.repaint();
	}
}
