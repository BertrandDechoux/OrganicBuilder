package uk.org.squirm3.listener;

import java.util.EventListener;

public interface IListener extends EventListener {
    public void propertyHasChanged();
}
