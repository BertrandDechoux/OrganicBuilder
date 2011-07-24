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

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.springframework.Messages;

public class ReactionListPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;
    
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

    private final MessageSource messageSource;

    public ReactionListPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource) {
        this.applicationEngine = applicationEngine;
        
        this.messageSource = messageSource;
        createListPanel();
        textArea = new JTextArea();

        final IListener reactionListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                reactionsHaveChanged();
            }
        };
        reactionListener.propertyHasChanged();
        applicationEngine.getEventDispatcher().addListener(
                reactionListener, EventDispatcher.Event.REACTIONS);

        updateDeleteButton();
    }

    private void createListPanel() {
        setLayout(new BorderLayout());
        border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                Messages.localize("reactions.current", messageSource));
        setBorder(border);
        listButtonsPanel = new JPanel();
        listButtonsPanel.setLayout(new GridLayout(4, 1));

        editButton = new JButton(Messages.localize("reactions.edit",
                messageSource));
        editButton.addActionListener(EventHandler.create(ActionListener.class,
                ReactionListPanel.this, "editReactions"));
        listButtonsPanel.add(editButton);

        updateButton = new JButton(Messages.localize("reactions.update",
                messageSource));
        updateButton
                .addActionListener(EventHandler.create(ActionListener.class,
                        ReactionListPanel.this, "updateReactions"));

        deleteButton = new JButton(Messages.localize("reactions.delete",
                messageSource));
        deleteButton.addActionListener(EventHandler.create(
                ActionListener.class, ReactionListPanel.this,
                "deleteSelectedReactions"));
        listButtonsPanel.add(deleteButton);

        clearButton = new JButton(Messages.localize("reactions.clear",
                messageSource));
        clearButton.addActionListener(EventHandler.create(ActionListener.class,
                applicationEngine, "clearReactions"));
        listButtonsPanel.add(clearButton);

        buttonParentPanel = new JPanel();
        buttonParentPanel.setLayout(new BorderLayout());
        buttonParentPanel.add(listButtonsPanel, BorderLayout.NORTH);
        add(buttonParentPanel, BorderLayout.EAST);

        reactionsList = new JList();
        reactionsList.getSelectionModel().addListSelectionListener(
                EventHandler.create(ListSelectionListener.class,
                        ReactionListPanel.this, "updateDeleteButton"));
        reactionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editReactions();
                }
            }
        });
        scrollPanel = new JScrollPane(reactionsList);
        add(scrollPanel, BorderLayout.CENTER);
    }

    public void editReactions() {
        buttonParentPanel.remove(listButtonsPanel);
        buttonParentPanel.add(updateButton, BorderLayout.NORTH);
        buttonParentPanel.updateUI();
        final Object[] reactions = applicationEngine.getReactions()
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
        final Vector<Reaction> v = new Vector<Reaction>();
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
            JOptionPane
                    .showMessageDialog(this, result, Messages.localize(
                            "reactions.parsing.error", messageSource),
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
        final Object[] reactions = reactionsList.getSelectedValues();
        final Collection<Reaction> c = new ArrayList<Reaction>(reactions.length);
        for (final Object reaction : reactions) {
            c.add((Reaction) reaction);
        }
        applicationEngine.removeReactions(c);
    }

    public void updateDeleteButton() {
        deleteButton.setEnabled(reactionsList.getSelectedValues().length != 0);
    }

    public void reactionsHaveChanged() {
        final Object[] reactions = applicationEngine.getReactions()
                .toArray();
        border.setTitle(Messages.localize("reactions.current", messageSource)
                + " (" + reactions.length + ")");
        clearButton.setEnabled(reactions.length != 0);
        reactionsList.setListData(reactions);
        this.repaint();
    }
}
