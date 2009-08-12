package uk.org.squirm3.listener;

import java.util.EventListener;

/**  
${my.copyright}
 */

public interface IStateListener extends EventListener {

    public void simulationStateHasChanged();
}
