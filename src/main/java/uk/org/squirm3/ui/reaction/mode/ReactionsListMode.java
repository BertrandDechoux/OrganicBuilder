package uk.org.squirm3.ui.reaction.mode;

import java.awt.Component;
import java.util.Collection;

import uk.org.squirm3.model.Reaction;

public interface ReactionsListMode {
    Component getMenu();
    Component getReactionsList();
    void reactionsHaveChanged(Collection<Reaction> reactions);
}
