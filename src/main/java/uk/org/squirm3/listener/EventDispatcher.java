package uk.org.squirm3.listener;

import java.util.*;

/**
 * ${my.copyright}
 */
public final class EventDispatcher {

    public enum Event {
        ATOMS, DRAGGING_POINT, LEVEL,
        CONFIGURATION, REACTIONS, SPEED, SIMULATION_STATE
    }

    private final Map<Event, Collection<IListener>> listeners;

    public EventDispatcher() {
        listeners = new HashMap<Event, Collection<IListener>>(Event.values().length);
        Event[] events = Event.values();
        for(int i = 0; i < events.length; i++) {
            listeners.put(events[i],new LinkedList<IListener>());
        }
    }

    public void addListener(IListener listener, Event event) {
        listeners.get(event).add(listener);
    }

    public void removeListener(IListener listener, Event event) {
        listeners.get(event).remove(listener);
    }

    public void dispatchEvent(Event event) {
        Iterator<IListener> iterator = listeners.get(event).iterator();
        while(iterator.hasNext()) {
            iterator.next().propertyHasChanged();
        }
    }
}
