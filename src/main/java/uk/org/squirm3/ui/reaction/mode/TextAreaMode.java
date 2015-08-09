package uk.org.squirm3.ui.reaction.mode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import com.google.common.base.Joiner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.ui.reaction.ReactionListPanel;

public class TextAreaMode implements ReactionsListMode {

	private final ReactionListPanel reactionListPanel;
	private final ConversionService conversionService;

	private Button updateButton;
	private TextArea textArea;

	public TextAreaMode(final ReactionListPanel reactionListPanel, final ConversionService conversionService) {
		this.reactionListPanel = reactionListPanel;
		this.conversionService = conversionService;

		Platform.runLater(() -> {
			updateButton = reactionListPanel.createButton("reactions.update", new UpdateReactionsHandler());
			textArea = new TextArea();
		});

	}

	@Override
	public Node getMenu() {
		return updateButton;
	}

	@Override
	public Node getReactionsList() {
		return textArea;
	}

	@Override
	public void reactionsHaveChanged(final Collection<Reaction> reactions) {
		textArea.setText(Joiner.on("\n").join(reactions));
	}

	private final class UpdateReactionsHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				reactionListPanel.setReactions(parseReactions(textArea.getText()));
			} catch (final UnparsableReactionException exception) {
				JOptionPane.showMessageDialog(reactionListPanel, exception.getUnparsableReaction(),
						reactionListPanel.localize("reactions.parsing.error"), JOptionPane.ERROR_MESSAGE);
			}
		}

		private Collection<Reaction> parseReactions(final String text) throws UnparsableReactionException {
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

		private Reaction parseReaction(final String line, final String originalLine)
				throws UnparsableReactionException {
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
