package uk.org.squirm3.ui.toolbar.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.level.Level;
import uk.org.squirm3.springframework.Messages;

public class LevelPicker extends JComboBox<String> {
    private static final long serialVersionUID = 1L;

    private boolean update = true;

    public LevelPicker(final ApplicationEngine applicationEngine,
            final MessageSource messageSource) {
        super(getLevelsLabels(applicationEngine));

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!update) {
                    applicationEngine.goToLevel(LevelPicker.this
                            .getSelectedIndex());
                }
                update = false;
            }
        });
        setToolTipText(Messages.localize("level.selected", messageSource));
        applicationEngine.addListener(new LevelListener(applicationEngine),
                ApplicationEngineEvent.LEVEL);
    }

    private static String[] getLevelsLabels(
            final ApplicationEngine applicationEngine) {
        final List<? extends Level> levelList = applicationEngine
                .getLevelManager().getLevels();
        final String[] levelsLabels = new String[levelList.size()];
        final Iterator<? extends Level> it = levelList.iterator();
        int i = 0;
        while (it.hasNext()) {
            String number = String.valueOf(i) + "  ";
            if (i < 10) {
                number += "  ";
            }
            levelsLabels[i] = number + it.next().getTitle();
            i++;
        }
        return levelsLabels;
    }

    private final class LevelListener implements Listener {
        private final ApplicationEngine applicationEngine;
        private LevelListener(final ApplicationEngine applicationEngine) {
            this.applicationEngine = applicationEngine;
        }
        @Override
        public void propertyHasChanged() {
            update = true;
            setSelectedIndex(applicationEngine.getLevelManager()
                    .getCurrentLevelIndex());
        }
    }

}
