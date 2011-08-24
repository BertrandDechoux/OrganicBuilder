package uk.org.squirm3.ui.reaction.mode;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.ui.reaction.ReactionListPanel;

public class JListMode implements ReactionsListMode {

    private final ReactionListPanel reactionListPanel;

    private final ReactionsListMode textMode;

    private final JButton editButton, deleteButton, clearButton;
    private final JList reactionsList;
    private final JPanel listButtonsPanel;

    public JListMode(final ReactionListPanel reactionListPanel,
            final ReactionsListMode textMode) {
        this.reactionListPanel = reactionListPanel;
        this.textMode = textMode;

        listButtonsPanel = new JPanel();
        listButtonsPanel.setLayout(new GridLayout(4, 1));

        final ActionListener editReactionListener = new EditReactionsListener();
        editButton = reactionListPanel.createJButton("reactions.edit",
                editReactionListener);
        listButtonsPanel.add(editButton);

        deleteButton = reactionListPanel.createJButton("reactions.delete",
                new DeleteSelectedReactionsListener());
        listButtonsPanel.add(deleteButton);

        clearButton = reactionListPanel.createJButton("reactions.clear",
                new ClearReactionsListener());
        listButtonsPanel.add(clearButton);

        reactionsList = new JList();
        reactionsList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(final ListSelectionEvent e) {
                        updateDeleteButton();
                    }
                });
        reactionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editReactionListener.actionPerformed(null);
                }
            }
        });
    }

    @Override
    public Component getMenu() {
        return listButtonsPanel;
    }

    @Override
    public Component getReactionsList() {
        return reactionsList;
    }

    @Override
    public void reactionsHaveChanged(final Collection<Reaction> reactions) {
        reactionsList.setListData(reactions.toArray());
        clearButton.setEnabled(!reactions.isEmpty());
        updateDeleteButton();
    }

    private void updateDeleteButton() {
        deleteButton.setEnabled(reactionsList.getSelectedValues().length != 0);
    }

    private final class DeleteSelectedReactionsListener
            implements
                ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            reactionListPanel.removeReactions(Arrays.asList((Reaction[]) //
                    reactionsList.getSelectedValues()));
        }
    }

    private final class ClearReactionsListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            reactionListPanel.clearReactions();
        }
    }

    private final class EditReactionsListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            reactionListPanel.setCurrentModeTo(textMode);
        }
    }
}
