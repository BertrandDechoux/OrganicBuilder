package uk.org.squirm3.ui.reaction.mode;

import java.util.Collection;

import javafx.scene.Node;
import uk.org.squirm3.model.Reaction;

public interface ReactionsListMode {
	Node getMenu();

	Node getReactionsList();

	void reactionsHaveChanged(Collection<Reaction> reactions);
}
