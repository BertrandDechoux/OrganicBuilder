package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
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
along with Foobar; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class ReactionsView implements IView, IReactionListener {
	private IApplicationEngine iApplicationEngine;
	private JCheckBox bondedBefore, bondedAfter;
	private JComboBox aType, aState, bType, bState, futureAState, futureBState;
	private JLabel futureAType, futureBType;
	private JButton addReaction, editButton, updateButton, deleteButton, clearButton;
	private JList reactionsList;
	private JScrollPane scrollPanel;
	private JTextArea textArea;
	private TitledBorder border;
	
	private final JPanel reactionsPanel;

	public ReactionsView(IApplicationEngine iApplicationEngine) {
		this.iApplicationEngine = iApplicationEngine;
		reactionsPanel = new JPanel();
		reactionsPanel.setLayout(new BorderLayout());
		reactionsPanel.add(createEditorPanel(),BorderLayout.NORTH);
		reactionsPanel.add(createListPanel(),BorderLayout.CENTER);
		textArea = new JTextArea();
		iApplicationEngine.addReactionListener(this);
	}
	
	public JPanel getReactionsPanel() {
		return reactionsPanel;
	}
	
	private JPanel createEditorPanel() {
		final JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.PAGE_AXIS));
		jPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
				Application.localize(new String[] {"interface","reactions","editor"})));
		final JPanel reactionPanel = new JPanel();
		reactionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		ActionListener l = createEditorActionListener();
		aType = createTypeComboBox();
		aType.addActionListener(l);
		reactionPanel.add(aType);
		aState = createStateComboBox();
		aState.addActionListener(l);
		reactionPanel.add(aState);
		bondedBefore = new JCheckBox();
		bondedBefore.addActionListener(l);
		reactionPanel.add(bondedBefore);
		bType = createTypeComboBox();
		bType.addActionListener(l);
		reactionPanel.add(bType);
		bState = createStateComboBox();
		bState.addActionListener(l);
		reactionPanel.add(bState);
		reactionPanel.add(new JLabel(" => "));
		futureAType = new JLabel();
		reactionPanel.add(futureAType);
		futureAState = createStateComboBox();
		futureAState.addActionListener(l);
		reactionPanel.add(futureAState);
		bondedAfter = new JCheckBox();
		bondedAfter.addActionListener(l);
		reactionPanel.add(bondedAfter);
		futureBType = new JLabel();
		reactionPanel.add(futureBType);
		futureBState = createStateComboBox();
		futureBState.addActionListener(l);
		reactionPanel.add(futureBState);
		jPanel.add(reactionPanel);
		addReaction = new JButton(Resource.getIcon("add"));
		addReaction.setMargin(new Insets(0,0,0,0));
		addReaction.setToolTipText(Application.localize(new String[] {"interface","reactions","add","tooltip"}));
		addReaction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Collection c = new ArrayList(1);
				c.add(new Reaction(aType.getSelectedIndex(),aState.getSelectedIndex(),bondedBefore.isSelected(),
						bType.getSelectedIndex(),bState.getSelectedIndex(),futureAState.getSelectedIndex(),
						bondedAfter.isSelected(),futureBState.getSelectedIndex()));
				iApplicationEngine.addReactions(c);
			}
		});
		jPanel.add(addReaction);
		l.actionPerformed(null); // init
		return jPanel;
	}
	
	private JPanel createListPanel() {
		final JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Application.localize(new String[] {"interface","reactions","current"}));
		jPanel.setBorder(border);
		final JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(4,1));
		editButton = new JButton(Application.localize(new String[] {"interface","reactions","edit"}));
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editReactions();
			}
		});
		buttonsPanel.add(editButton);
		updateButton = new JButton(Application.localize(new String[] {"interface","reactions","update"}));
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Vector v = new Vector();
				String result = Reaction.parse(textArea.getText(), v);
				if(result!=null) {
					JOptionPane.showMessageDialog(jPanel,result,Application.localize(new String[] {"interface","reactions","parsing","error"}), JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					if(v.size()!=0) {
						iApplicationEngine.setReactions(v);
					}
					editButton.setEnabled(true);
					updateButton.setEnabled(false);
					addReaction.setEnabled(true);
					deleteButton.setEnabled(!reactionsList.isSelectionEmpty());
					clearButton.setEnabled(reactionsList.getModel().getSize()!=0);
					scrollPanel.setViewportView(reactionsList);
				}
			}
		});
		updateButton.setEnabled(false);
		buttonsPanel.add(updateButton);
		deleteButton = new JButton(Application.localize(new String[] {"interface","reactions","delete"}));
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] reactions = reactionsList.getSelectedValues();
				Collection c = new ArrayList(reactions.length);
				for(int i = 0; i < reactions.length ; i++) {
					c.add(reactions[i]);
				}
				iApplicationEngine.removeReactions(c);
			}
		});
		deleteButton.setEnabled(false);
		
		buttonsPanel.add(deleteButton);
		clearButton = new JButton(Application.localize(new String[] {"interface","reactions","clear"}));
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				iApplicationEngine.clearReactions();
			}
		});
		buttonsPanel.add(clearButton);
		final JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new BorderLayout());
		tempPanel.add(buttonsPanel, BorderLayout.NORTH);
		jPanel.add(tempPanel, BorderLayout.EAST);
		reactionsList = new JList();
		reactionsList.getSelectionModel().addListSelectionListener(
				new ListSelectionListener(){
					public void valueChanged(ListSelectionEvent arg0) {
						deleteButton.setEnabled(reactionsList.getSelectedValues().length!=0);
					}	
				});
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
	
	private JComboBox createTypeComboBox() {
		JComboBox jComboBox = new JComboBox();
		for(int i=0;i<8;i++) {
			jComboBox.addItem(String.valueOf(Atom.type_code.charAt(i)));
		}
		return jComboBox;
	}
	
	private JComboBox createStateComboBox() {
		JComboBox jComboBox = new JComboBox();
		for(int i=0;i<50;i++) { //TODO no hardcoded value!
			jComboBox.addItem(String.valueOf(i));
		}
		return jComboBox;
	}
	
	private ActionListener createEditorActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				futureAType.setText(String.valueOf(Atom.type_code.charAt(aType.getSelectedIndex())));
				futureBType.setText(String.valueOf(Atom.type_code.charAt(bType.getSelectedIndex())));
				String reactionString = "";
				reactionString += aType.getSelectedItem();
				reactionString += aState.getSelectedItem();
				if(!bondedBefore.isSelected()) reactionString +=" + ";
				reactionString += bType.getSelectedItem();
				reactionString += bState.getSelectedItem();
				reactionString += " => ";
				reactionString += futureAType.getText();
				reactionString += futureAState.getSelectedItem();
				if(!bondedAfter.isSelected()) reactionString +=" + ";
				reactionString += futureBType.getText();
				reactionString += futureBState.getSelectedItem();
				addReaction.setText(reactionString);
			}
		};
	}
	
	private void editReactions() {
		editButton.setEnabled(false);
		updateButton.setEnabled(true);
		addReaction.setEnabled(false);
		deleteButton.setEnabled(false);
		clearButton.setEnabled(false);
		Object[] reactions = iApplicationEngine.getReactions().toArray();
		String content = "";
		for(int i = 0 ; i<reactions.length ; i++) {
			content += reactions[i].toString()+"\n";
		}
		textArea.setText(content);
		scrollPanel.setViewportView(textArea);
	}

	public void reactionsHaveChanged() {
		Object[] reactions = iApplicationEngine.getReactions().toArray();
		border.setTitle(Application.localize(new String[] {"interface","reactions","current"})+" ("+reactions.length+")");
		clearButton.setEnabled(reactions.length!=0);
		reactionsList.setListData(reactions);
		reactionsPanel.repaint();
	}

	public void isVisible(boolean b) {
		// TODO Auto-generated method stub
	}
}
