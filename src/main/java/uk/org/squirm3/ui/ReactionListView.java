package uk.org.squirm3.ui;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Reaction;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.IReactionListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * ${my.copyright}
 */

public class ReactionListView implements IView, IReactionListener {
    // commucation with the engine
    private ApplicationEngine applicationEngine;
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

    public ReactionListView(ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
        listPanel = createListPanel();
        textArea = new JTextArea();
        applicationEngine.getEngineDispatcher().addReactionListener(this);
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
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Application.localize(new String[]{"interface", "reactions", "current"}));
        jPanel.setBorder(border);
        listButtonsPanel = new JPanel();
        listButtonsPanel.setLayout(new GridLayout(4, 1));

        editButton = new JButton(Application.localize(new String[]{"interface", "reactions", "edit"}));
        editButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ReactionListView.this, "editReactions"));
        listButtonsPanel.add(editButton);

        updateButton = new JButton(Application.localize(new String[]{"interface", "reactions", "update"}));
        updateButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ReactionListView.this, "updateReactions"));

        deleteButton = new JButton(Application.localize(new String[]{"interface", "reactions", "delete"}));
        deleteButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ReactionListView.this, "deleteSelectedReactions"));
        listButtonsPanel.add(deleteButton);

        clearButton = new JButton(Application.localize(new String[]{"interface", "reactions", "clear"}));
        clearButton.addActionListener((ActionListener) EventHandler.create(ActionListener.class, applicationEngine, "clearReactions"));
        listButtonsPanel.add(clearButton);

        buttonParentPanel = new JPanel();
        buttonParentPanel.setLayout(new BorderLayout());
        buttonParentPanel.add(listButtonsPanel, BorderLayout.NORTH);
        jPanel.add(buttonParentPanel, BorderLayout.EAST);

        reactionsList = new JList();
        reactionsList.getSelectionModel().addListSelectionListener(
                (ListSelectionListener) EventHandler.create(ListSelectionListener.class, ReactionListView.this, "updateDeleteButton"));
        reactionsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editReactions();
                }
            }
        });
        scrollPanel = new JScrollPane(reactionsList);
        jPanel.add(scrollPanel, BorderLayout.CENTER);
        return jPanel;
    }

    public void editReactions() {
        buttonParentPanel.remove(listButtonsPanel);
        buttonParentPanel.add(updateButton, BorderLayout.NORTH);
        buttonParentPanel.updateUI();
        Object[] reactions = applicationEngine.getReactions().toArray();
        String content = "";
        for (int i = 0; i < reactions.length; i++) {
            content += reactions[i].toString() + "\n";
        }
        textArea.setText(content);
        scrollPanel.setViewportView(textArea);
    }

    public void updateReactions() {
        String result = null;
        Vector v = new Vector();
        // System.out.println("Input: "+text); // DEBUG
        // for each line in the text			
        StringTokenizer lines = new StringTokenizer(textArea.getText(), "\n", true);
        String line = new String();
        while (lines.hasMoreTokens()) {
            line = lines.nextToken();
            line = line.trim(); // remove leading and trailing whitespace and control chars
            if (line.length() == 0) continue; // nothing doing
            if (line.length() > 2 && line.charAt(0) == '/' && line.charAt(1) == '/')
                continue; // this line contains a comment, skip it
            // System.out.println("Parsing line: "+line+" (length "+String.valueOf(line.length())+")");
            Reaction r = Reaction.parse(line);
            // System.out.println(r.getString()+"\n"); // DEBUG
            if (r != null) v.add(r);
            else result = line;
        }

        if (result != null) {
            JOptionPane.showMessageDialog(listPanel, result,
                    Application.localize(new String[]{"interface", "reactions", "parsing", "error"}),
                    JOptionPane.ERROR_MESSAGE);
        } else {
            applicationEngine.setReactions(v);
            reactionsHaveChanged();
            buttonParentPanel.remove(updateButton);
            buttonParentPanel.add(listButtonsPanel, BorderLayout.NORTH);
            scrollPanel.setViewportView(reactionsList);
        }
    }

    public void deleteSelectedReactions() {
        Object[] reactions = reactionsList.getSelectedValues();
        Collection c = new ArrayList(reactions.length);
        for (int i = 0; i < reactions.length; i++) {
            c.add(reactions[i]);
        }
        applicationEngine.removeReactions(c);
    }

    public void updateDeleteButton() {
        deleteButton.setEnabled(reactionsList.getSelectedValues().length != 0);
    }

    public void reactionsHaveChanged() {
        Object[] reactions = applicationEngine.getReactions().toArray();
        border.setTitle(Application.localize(new String[]{"interface", "reactions", "current"}) + " (" + reactions.length + ")");
        clearButton.setEnabled(reactions.length != 0);
        reactionsList.setListData(reactions);
        listPanel.repaint();
    }
}
