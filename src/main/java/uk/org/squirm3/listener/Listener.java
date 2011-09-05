package uk.org.squirm3.listener;

import java.util.EventListener;

/**
 * A simple generic listener.
 */
public interface Listener extends EventListener {
    public void propertyHasChanged();
}
