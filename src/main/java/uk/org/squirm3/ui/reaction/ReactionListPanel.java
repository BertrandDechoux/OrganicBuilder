package uk.org.squirm3.ui.reaction;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.reaction.mode.JListMode;
import uk.org.squirm3.ui.reaction.mode.JTextAreaMode;
import uk.org.squirm3.ui.reaction.mode.ReactionsListMode;

public class ReactionListPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;
    private final MessageSource messageSource;

    private JPanel buttonParentPanel;
    private JScrollPane scrollPanel;
    private TitledBorder border;

    private final ReactionsListMode defaultMode;
    private ReactionsListMode currentMode;

    public ReactionListPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource,
            final ConversionService conversionService) {
        this.applicationEngine = applicationEngine;
        this.messageSource = messageSource;

        currentMode = new JTextAreaMode(this, conversionService);
        defaultMode = new JListMode(this, currentMode);

        createListPanel();
        setCurrentModeTo(defaultMode);

        applicationEngine.addListener(new ReactionsChangedListener(),
                ApplicationEngineEvent.REACTIONS);
    }

    public void setCurrentModeTo(final ReactionsListMode futureMode) {
        buttonParentPanel.remove(currentMode.getMenu());
        buttonParentPanel.add(futureMode.getMenu(), BorderLayout.NORTH);
        buttonParentPanel.updateUI();
        scrollPanel.setViewportView(futureMode.getReactionsList());
        currentMode = futureMode;
        currentMode.reactionsHaveChanged(applicationEngine.getReactions());
    }

    public JButton createJButton(final String key,
            final ActionListener actionListener) {
        final JButton jButton = new JButton(Messages.localize(key,
                messageSource));
        jButton.addActionListener(actionListener);
        return jButton;
    }

    public void setReactions(final Collection<Reaction> reactions) {
        applicationEngine.setReactions(reactions);
    }

    public void removeReactions(final Collection<Reaction> reactions) {
        applicationEngine.removeReactions(reactions);
    }

    public void clearReactions() {
        applicationEngine.clearReactions();
    }

    public String localize(final String key) {
        return Messages.localize("reactions.current", messageSource);
    }

    private final class ReactionsChangedListener implements Listener {
        @Override
        public void propertyHasChanged() {
            final Collection<Reaction> reactions = applicationEngine
                    .getReactions();
            border.setTitle(localize("reactions.current") + " ("
                    + reactions.size() + ")");
            setCurrentModeTo(defaultMode);
            ReactionListPanel.this.repaint();
        }
    }

    private void createListPanel() {
        setLayout(new BorderLayout());
        border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                localize("reactions.current"));
        setBorder(border);

        buttonParentPanel = new JPanel();
        buttonParentPanel.setLayout(new BorderLayout());
        add(buttonParentPanel, BorderLayout.EAST);
        scrollPanel = new JScrollPane();
        add(scrollPanel, BorderLayout.CENTER);
    }
}
