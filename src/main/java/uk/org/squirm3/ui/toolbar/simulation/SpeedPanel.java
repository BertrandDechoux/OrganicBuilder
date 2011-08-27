package uk.org.squirm3.ui.toolbar.simulation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.swing.SwingUtils;

/**
 * Allows user to change the speed of the simulation.
 */
public class SpeedPanel extends JPanel implements Listener {
    private static final long serialVersionUID = 1L;

    private final ApplicationEngine applicationEngine;
    private final JSlider speedSelector;

    public SpeedPanel(final ApplicationEngine applicationEngine,
            final MessageSource messageSource) {
        this.applicationEngine = applicationEngine;
        this.speedSelector = createSpeedSelector();
        setupLayout(messageSource);
        applicationEngine.addListener(this, ApplicationEngineEvent.SPEED);

    }

    private void setupLayout(final MessageSource messageSource) {
        setLayout(new GridBagLayout());
        add(new JLabel(Messages.localize("parameters.speed", messageSource)),
                SwingUtils.createCustomGBC(0, 0));
        add(speedSelector, SwingUtils.createCustomGBC(1, 0, 80,
                GridBagConstraints.HORIZONTAL));
    }

    private JSlider createSpeedSelector() {
        final JSlider speedSelector = new JSlider(1, 8);
        speedSelector.setMajorTickSpacing(1);
        speedSelector.setPaintTicks(true);
        speedSelector.setInverted(true);
        speedSelector
                .addChangeListener(new SpeedSelectorListener(speedSelector));
        return speedSelector;
    }

    /**
     * Convert non-linear mapping : selector to engine.
     */
    private void updateEngineSpeed() {
        applicationEngine.setSimulationSpeed((short) (Math.pow(
                speedSelector.getValue(), 2)));
    }

    /**
     * Convert non-linear mapping : engine to selector.
     */
    @Override
    public void propertyHasChanged() {
        speedSelector.setValue((int) Math.sqrt(applicationEngine
                .getSimulationSpeed()));
    }

    /**
     * When the user has picked a new value for the selector, update the engine.
     */
    private final class SpeedSelectorListener implements ChangeListener {
        private final JSlider speedSelector;

        private SpeedSelectorListener(JSlider speedSelector) {
            this.speedSelector = speedSelector;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.
         * ChangeEvent)
         */
        @Override
        public void stateChanged(final ChangeEvent e) {
            final Object object = e.getSource();
            if (object != speedSelector) {
                return;
            }
            if (!speedSelector.getValueIsAdjusting()) {
                updateEngineSpeed();
            }
        }

    }
}
