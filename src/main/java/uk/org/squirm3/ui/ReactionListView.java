package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
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
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;

public class ReactionListView extends AView {
    // list mode
    private JButton editButton, deleteButton, clearButton;
    private JList reactionsList;
    private JPanel listButtonsPanel;
    // edit mode
    private JButton updateButton;
    private final JTextArea textArea;
    // main components
    private JPanel buttonParentPanel;
    private JScrollPane scrollPanel;
    private TitledBorder border;
    private final JPanel listPanel;

    public ReactionListView(final ApplicationEngine applicationEngine) {
        super(applicationEngine);
        listPanel = createListPanel();
        textArea = new JTextArea();

        final IListener reactionListener = new IListener() {
            public void propertyHasChanged() {
                reactionsHaveChanged();
            }
        };
        reactionListener.propertyHasChanged();
        getApplicationEngine().getEventDispatcher().addListener(
                reactionListener, EventDispatcher.Event.REACTIONS);

        updateDeleteButton();
    }

    public JPanel getListPanel() {
        return listPanel;
    }

    private JPanel createListPanel() {
        final JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                Application.localize("reactions.current"));
        jPanel.setBorder(border);
        listButtonsPanel = new JPanel();
        listButtonsPanel.setLayout(new GridLayout(4, 1));

        editButton = new JButton(Application.localize("reactions.edit"));
        editButton.addActionListener(EventHandler.create(ActionListener.class,
                ReactionListView.this, "editReactions"));
        listButtonsPanel.add(editButton);

        updateButton = new JButton(Application.localize("reactions.update"));
        updateButton
                .addActionListener(EventHandler.create(ActionListener.class,
                        ReactionListView.this, "updateReactions"));

        deleteButton = new JButton(Application.localize("reactions.delete"));
        deleteButton.addActionListener(EventHandler.create(
                ActionListener.class, ReactionListView.this,
                "deleteSelectedReactions"));
        listButtonsPanel.add(deleteButton);

        clearButton = new JButton(Application.localize("reactions.clear"));
        clearButton.addActionListener(EventHandler.create(ActionListener.class,
                getApplicationEngine(), "clearReactions"));
        listButtonsPanel.add(clearButton);

        buttonParentPanel = new JPanel();
        buttonParentPanel.setLayout(new BorderLayout());
        buttonParentPanel.add(listButtonsPanel, BorderLayout.NORTH);
        jPanel.add(buttonParentPanel, BorderLayout.EAST);

        reactionsList = new JList();
        reactionsList.getSelectionModel().addListSelectionListener(
                EventHandler.create(ListSelectionListener.class,
                        ReactionListView.this, "updateDeleteButton"));
        reactionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
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
        final Object[] reactions = getApplicationEngine().getReactions()
                .toArray();
        String content = "";
        for (final Object reaction : reactions) {
            content += reaction.toString() + "\n";
        }
        textArea.setText(content);
        scrollPanel.setViewportView(textArea);
    }

    public void updateReactions() {
        String result = null;
        final Vector v = new Vector();
        // System.out.println("Input: "+text); // DEBUG
        // for each line in the text
        final StringTokenizer lines = new StringTokenizer(textArea.getText(),
                "\n", true);
        String line = new String();
        while (lines.hasMoreTokens()) {
            line = lines.nextToken();
            line = line.trim(); // remove leading and trailing whitespace and
                                // control chars
            if (line.length() == 0) {
                continue; // nothing doing
            }
            if (line.length() > 2 && line.charAt(0) == '/'
                    && line.charAt(1) == '/') {
                continue; // this line contains a comment, skip it
            }
            // System.out.println("Parsing line: "+line+" (length "+String.valueOf(line.length())+")");
            final Reaction r = Reaction.parse(line);
            // System.out.println(r.getString()+"\n"); // DEBUG
            if (r != null) {
                v.add(r);
            } else {
                result = line;
            }
        }

        if (result != null) {
            JOptionPane.showMessageDialog(listPanel, result,
                    Application.localize("reactions.parsing.error"),
                    JOptionPane.ERROR_MESSAGE);
        } else {
            getApplicationEngine().setReactions(v);
            reactionsHaveChanged();
            buttonParentPanel.remove(updateButton);
            buttonParentPanel.add(listButtonsPanel, BorderLayout.NORTH);
            scrollPanel.setViewportView(reactionsList);
        }
    }

    public void deleteSelectedReactions() {
        final Object[] reactions = reactionsList.getSelectedValues();
        final Collection c = new ArrayList(reactions.length);
        for (final Object reaction : reactions) {
            c.add(reaction);
        }
        getApplicationEngine().removeReactions(c);
    }

    public void updateDeleteButton() {
        deleteButton.setEnabled(reactionsList.getSelectedValues().length != 0);
    }

    public void reactionsHaveChanged() {
        final Object[] reactions = getApplicationEngine().getReactions()
                .toArray();
        border.setTitle(Application.localize("reactions.current") + " ("
                + reactions.length + ")");
        clearButton.setEnabled(reactions.length != 0);
        reactionsList.setListData(reactions);
        listPanel.repaint();
    }
}
