package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.springframework.context.MessageSource;

import uk.org.squirm3.springframework.Messages;
import uk.org.squirm3.ui.view.AtomsView;

public class GUI {
    public static final Color BACKGROUND = new Color(255, 255, 225);

    public GUI(final MessageSource messageSource,
            final CurrentLevelPanel currentLevelPanel,
            final ReactionListPanel reactionListPanel,
            final LevelsControlPanel levelsControlPanel,
            final ReactionEditorPanel reactionEditorPanel,
            final AtomsView atomsView, final ToolBarPanel toolBarPanel) {

        // main panels
        final JComponent collisionsPanel = atomsView.getCollisionsPanel();
        final JSplitPane reactionsPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, true, reactionEditorPanel,
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

        final JSplitPane rootComponent = buildRootComponent(collisionsPanel,
                currentLevelPanel, reactionsPane);
        final JPanel contentPane = buildContentPane(toolBarPanel, rootComponent);
        buildMainFrame(messageSource, contentPane);
    }

    private JSplitPane buildRootComponent(final JComponent collisionsPanel,
            final JPanel currentLevelPanel, final JSplitPane reactionsPane) {
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

    private JPanel buildContentPane(final ToolBarPanel toolBarPanel,
            final JSplitPane rootComponent) {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBarPanel, BorderLayout.NORTH);
        contentPane.add(rootComponent, BorderLayout.CENTER);
        return contentPane;
    }

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

    public static JButton createIconButton(final Action action) {
        final JButton button = new JButton(action);
        button.setMargin(new Insets(-3, -3, -3, -3));
        button.setBorderPainted(false);
        button.setBackground(BACKGROUND);
        return button;
    }

}
