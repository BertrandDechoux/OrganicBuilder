package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.springframework.context.MessageSource;

import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.collider.AtomsPanel;
import uk.org.squirm3.ui.level.CurrentLevelPanel;
import uk.org.squirm3.ui.reaction.ReactionConstructorPanel;
import uk.org.squirm3.ui.reaction.ReactionListPanel;
import uk.org.squirm3.ui.toolbar.ToolBarPanel;

/**
 * Root of the graphical user interface. It setups the injected dependencies
 * within a layout.
 */
public class GUI {

    public GUI(final MessageSource messageSource,
            final CurrentLevelPanel currentLevelPanel,
            final ReactionListPanel reactionListPanel,
            final ReactionConstructorPanel reactionConstructorPanel,
            final AtomsPanel collisionsPanel, final ToolBarPanel toolBarPanel) {

        final JSplitPane reactionsPane = createReactionsPane(reactionListPanel,
                reactionConstructorPanel);
        final JSplitPane rootComponent = buildRootComponent(collisionsPanel,
                currentLevelPanel, reactionsPane);
        final JPanel contentPane = buildContentPane(toolBarPanel, rootComponent);
        buildMainFrame(messageSource, contentPane);
    }

    /**
     * Bottom-left component.
     */
    private JSplitPane createReactionsPane(
            final ReactionListPanel reactionListPanel,
            final ReactionConstructorPanel reactionConstructorPanel) {
        final JSplitPane reactionsPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, true, reactionConstructorPanel,
                reactionListPanel);
        reactionsPane.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent arg0) {
                if (arg0.getPropertyName().equals("dividerLocation")
                        && !arg0.getNewValue().toString().equals("1")) {
                    reactionsPane.setDividerLocation(-1);
                }
            }
        });
        reactionsPane.setOneTouchExpandable(true);
        return reactionsPane;
    }

    /**
     * The main component (without the toolbar)
     */
    private JSplitPane buildRootComponent(final JComponent collisionsPanel,
            final CurrentLevelPanel currentLevelPanel, final JSplitPane reactionsPane) {
        final JSplitPane leftComponent = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, true, currentLevelPanel,
                reactionsPane);
        leftComponent.setOneTouchExpandable(true);
        final JSplitPane rootComponent = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, true, leftComponent,
                collisionsPanel);
        rootComponent.setOneTouchExpandable(true);
        return rootComponent;
    }

    /**
     * The whole graphical user interface.
     */
    private JPanel buildContentPane(final ToolBarPanel toolBarPanel,
            final JSplitPane rootComponent) {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBarPanel, BorderLayout.NORTH);
        contentPane.add(rootComponent, BorderLayout.CENTER);
        return contentPane;
    }

    /**
     * Setup the whole GUI, title, size and exit behavior.
     */
    private void buildMainFrame(final MessageSource messageSource,
            final JPanel contentPane) {
        final JFrame frame = new JFrame(Messages.localize("application.title",
                messageSource));
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(contentPane);
        SwingUtilities.updateComponentTreeUI(frame);
        frame.setVisible(true);
    }

}
