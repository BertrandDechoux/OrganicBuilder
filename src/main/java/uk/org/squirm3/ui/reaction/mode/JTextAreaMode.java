package uk.org.squirm3.ui.reaction.mode;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.ui.reaction.ReactionListPanel;

import com.google.common.base.Joiner;

public class JTextAreaMode implements ReactionsListMode {

    private final ReactionListPanel reactionListPanel;
    private final ConversionService conversionService;

    private final JButton updateButton;
    private final JTextArea textArea;

    public JTextAreaMode(final ReactionListPanel reactionListPanel,
            final ConversionService conversionService) {
        this.reactionListPanel = reactionListPanel;
        this.conversionService = conversionService;

        updateButton = reactionListPanel.createJButton("reactions.update",
                new UpdateReactionsListener());
        textArea = new JTextArea();

    }

    @Override
    public Component getMenu() {
        return updateButton;
    }

    @Override
    public Component getReactionsList() {
        return textArea;
    }

    @Override
    public void reactionsHaveChanged(final Collection<Reaction> reactions) {
        textArea.setText(Joiner.on("\n").join(reactions));
    }

    private final class UpdateReactionsListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                reactionListPanel.setReactions(parseReactions(textArea
                        .getText()));
            } catch (final UnparsableReactionException exception) {
                JOptionPane.showMessageDialog(reactionListPanel,
                        exception.getUnparsableReaction(),
                        reactionListPanel.localize("reactions.parsing.error"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private Collection<Reaction> parseReactions(final String text)
                throws UnparsableReactionException {
            final List<Reaction> reactions = new ArrayList<Reaction>();
            final StringTokenizer lines = new StringTokenizer(text, "\n", true);

            String line = null;
            String originalLine = null;

            while (lines.hasMoreTokens()) {
                originalLine = lines.nextToken();
                line = StringUtils.trimAllWhitespace(originalLine);
                if (!StringUtils.hasText(line) || isAComment(line)) {
                    continue;
                }
                final Reaction r = parseReaction(line, originalLine);
                if (r != null) {
                    reactions.add(r);
                } else {
                    throw new UnparsableReactionException(originalLine);
                }
            }
            return reactions;
        }

        private Reaction parseReaction(final String line,
                final String originalLine) throws UnparsableReactionException {
            try {
                return conversionService.convert(line, Reaction.class);
            } catch (final Exception e) {
                throw new UnparsableReactionException(originalLine);
            }
        }

        private boolean isAComment(final String line) {
            return line.startsWith("/") || line.startsWith("#");
        }

        public class UnparsableReactionException extends Exception {
            private static final long serialVersionUID = 1L;
            private final String unparsableReaction;

            private UnparsableReactionException(final String unparsableReaction) {
                this.unparsableReaction = unparsableReaction;
            }

            public String getUnparsableReaction() {
                return unparsableReaction;
            }

        }
    }

}
