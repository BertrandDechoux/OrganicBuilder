package uk.org.squirm3.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.springframework.Messages;

public class IntroAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;

    public IntroAction(final ApplicationEngine applicationEngine,
            final MessageSource messageSource, final ImageIcon introIcon) {
        this.applicationEngine = applicationEngine;
        putValue(Action.SHORT_DESCRIPTION,
                Messages.localize("navigation.first", messageSource));
        putValue(Action.SMALL_ICON, introIcon);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        applicationEngine.goToFirstLevel();
    }
}
