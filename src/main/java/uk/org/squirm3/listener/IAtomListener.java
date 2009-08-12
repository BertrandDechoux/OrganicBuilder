package uk.org.squirm3.listener;

import java.util.EventListener;

/**
 * ${my.copyright}
 */

public interface IAtomListener extends EventListener {

    public void atomsHaveChanged();

    public void draggingPointHasChanged();
}
