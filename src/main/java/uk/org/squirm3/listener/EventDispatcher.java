package uk.org.squirm3.listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public final class EventDispatcher {

    public enum Event {
        ATOMS, DRAGGING_POINT, LEVEL, CONFIGURATION, REACTIONS, SPEED, SIMULATION_STATE
    }

    private final Map<Event, Collection<IListener>> listeners;

    public EventDispatcher() {
        listeners = new HashMap<Event, Collection<IListener>>(
                Event.values().length);
        final Event[] events = Event.values();
        for (final Event event : events) {
            listeners.put(event, new LinkedList<IListener>());
        }
    }

    public void addListener(final IListener listener, final Event event) {
        listeners.get(event).add(listener);
    }

    public void removeListener(final IListener listener, final Event event) {
        listeners.get(event).remove(listener);
    }

    public void dispatchEvent(final Event event) {
        final Iterator<IListener> iterator = listeners.get(event).iterator();
        while (iterator.hasNext()) {
            iterator.next().propertyHasChanged();
        }
    }
}
