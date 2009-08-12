package uk.org.squirm3.engine;

import uk.org.squirm3.data.Reaction;

import java.util.*;

/**
 * ${my.copyright}
 */

public class ReactionManager {
    private List reactions = new LinkedList();
    private List Immutablereactions = Collections.unmodifiableList(reactions);

    public void addReaction(Reaction r) {
        reactions.add(r);
    }

    public void addReactions(Collection c) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Reaction) {
                Reaction r = (Reaction) o;
                reactions.add(r);
            }
        }
    }

    public void removeReactions(Reaction r) {
        reactions.remove(r);
    }

    public void removeReactions(Collection c) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Reaction) {
                Reaction r = (Reaction) o;
                reactions.remove(r);
            }
        }
    }

    public void clearReactions() {
        reactions.clear();
    }

    public List getReactions() {
        return Immutablereactions;
    }
}
