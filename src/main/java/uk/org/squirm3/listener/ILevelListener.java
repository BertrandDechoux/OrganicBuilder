package uk.org.squirm3.listener;

import java.util.EventListener;

/**
 * ${my.copyright}
 */

public interface ILevelListener extends EventListener {

    public void levelHasChanged();

    public void configurationHasChanged();

}
