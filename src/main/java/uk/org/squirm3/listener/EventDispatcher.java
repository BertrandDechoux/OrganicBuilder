package uk.org.squirm3.listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Binds and fires {@link Listener} for a defined event.
 */
public final class EventDispatcher<E> {

    private final Map<E, Collection<Listener>> listeners;

    public EventDispatcher() {
        listeners = new HashMap<E, Collection<Listener>>();
    }

    /**
     * Binds a listener to an event. It will then be called by
     * {@link #dispatchEvent(Object)}.
     */
    public void addListener(final Listener listener, final E event) {
        if (!listeners.containsKey(event)) {
            listeners.put(event, new LinkedList<Listener>());
        }
        listeners.get(event).add(listener);
    }

    /**
     * Calls all listeners associated with an event.
     */
    public void dispatchEvent(final E event) {
        if (!listeners.containsKey(event)) {
            listeners.put(event, new LinkedList<Listener>());
        }
        for (final Listener listener : listeners.get(event)) {
            listener.propertyHasChanged();
        }
    }
}
