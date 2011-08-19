package uk.org.squirm3.ui.toolbar.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher.Event;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.springframework.Messages;

public abstract class SquirmAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private final MessageSource messageSource;
    private final ApplicationEngine applicationEngine;

    public SquirmAction(final MessageSource messageSource,
            final ApplicationEngine applicationEngine,
            final String shortDescription, final ImageIcon smallIcon) {
        this.messageSource = messageSource;
        this.applicationEngine = applicationEngine;

        putValue(Action.SHORT_DESCRIPTION,
                Messages.localize(shortDescription, messageSource));
        putValue(Action.SMALL_ICON, smallIcon);
    }

    protected final ApplicationEngine getApplicationEngine() {
        return applicationEngine;
    }

    protected final void addListener(final IListener listener, final Event event) {
        listener.propertyHasChanged();
        applicationEngine.getEventDispatcher().addListener(listener, event);
    }
    
    protected final String localize(final String key) {
        return Messages.localize(key, messageSource);
    }

}
