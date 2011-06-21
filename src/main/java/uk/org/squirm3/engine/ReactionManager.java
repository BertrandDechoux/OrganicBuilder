package uk.org.squirm3.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uk.org.squirm3.model.Reaction;

public class ReactionManager {
    private final List<Reaction> reactions = new LinkedList<Reaction>();
    private final List<Reaction> Immutablereactions = Collections
            .unmodifiableList(reactions);

    public void addReaction(final Reaction r) {
        reactions.add(r);
    }

    public void addReactions(final Collection<Reaction> c) {
        final Iterator<Reaction> it = c.iterator();
        while (it.hasNext()) {
            final Object o = it.next();
            if (o instanceof Reaction) {
                final Reaction r = (Reaction) o;
                reactions.add(r);
            }
        }
    }

    public void removeReactions(final Reaction r) {
        reactions.remove(r);
    }

    public void removeReactions(final Collection<Reaction> c) {
        final Iterator<Reaction> it = c.iterator();
        while (it.hasNext()) {
            final Object o = it.next();
            if (o instanceof Reaction) {
                final Reaction r = (Reaction) o;
                reactions.remove(r);
            }
        }
    }

    public void clearReactions() {
        reactions.clear();
    }

    public List<Reaction> getReactions() {
        return Immutablereactions;
    }
}
