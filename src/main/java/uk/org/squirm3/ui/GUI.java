package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.springframework.context.MessageSource;

import uk.org.squirm3.engine.ApplicationEngine;

public class GUI {

    private static MessageSource messageSource;

    public static void createGUI(final ApplicationEngine applicationEngine, final Action aboutAction, final LevelNavigatorView levelNavigatorView, final StateView stateView, final ReactionEditorView reactionEditorView, final AtomsView atomsView, final ImageIcon parameterIcon) {
        // frame
        final JFrame frame = new JFrame(GUI.localize("application.title"));
        frame.setSize(1080, 630);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // view
        final CurrentLevelView currentLevelView = new CurrentLevelView(
                applicationEngine);
        final ReactionListView reactionListView = new ReactionListView(
                applicationEngine);
        final CustomResetView customResetView = new CustomResetView(
                applicationEngine);
        final SpeedView speedView = new SpeedView(applicationEngine);

        // main panels
        final JComponent collisionsPanel = atomsView.getCollisionsPanel();
        final JPanel currentLevelPanel = currentLevelView
                .getCurrentLevelPanel();
        final JPanel reactionEditorPanel = reactionEditorView.getEditorPanel();
        reactionEditorPanel
                .setMaximumSize(reactionEditorPanel.getMinimumSize());

        final JSplitPane reactionsPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, true,
                reactionEditorView.getEditorPanel(),
                reactionListView.getListPanel());
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

        // toolbar
        final JPanel toolBar = new JPanel();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        final Color bg = new Color(255, 255, 225);
        toolBar.setBackground(bg);
        // simulation controls
        final JPanel simControlsPanel = new JPanel();
        simControlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 2));
        simControlsPanel.setBackground(bg);
        simControlsPanel.add(createIconButton(stateView.getStopAction(), bg));
        simControlsPanel.add(createIconButton(stateView.getRunAction(), bg));
        simControlsPanel.add(createIconButton(stateView.getResetAction(), bg));
        simControlsPanel
                .add(createIconButton(
                        createParametersAction(customResetView.getPanel(),
                                speedView.getPanel(),
                                atomsView.getControlsPanel(), parameterIcon), bg));
        toolBar.add(simControlsPanel);
        toolBar.add(Box.createHorizontalGlue());
        // navigation controls
        final JPanel navControlsPanel = new JPanel();
        navControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
        navControlsPanel.setBackground(bg);
        navControlsPanel.add(createIconButton(
                levelNavigatorView.getIntroAction(), bg));
        navControlsPanel.add(createIconButton(
                levelNavigatorView.getPreviousAction(), bg));
        final JComboBox cb = levelNavigatorView.getLevelComboBox();
        cb.setMaximumSize(new Dimension(150, 80));
        navControlsPanel.add(cb);
        navControlsPanel.add(createIconButton(
                levelNavigatorView.getNextAction(), bg));
        navControlsPanel.add(createIconButton(
                levelNavigatorView.getLastAction(), bg));
        toolBar.add(navControlsPanel);
        toolBar.add(Box.createHorizontalGlue());
        // misc controls
        final JPanel miscControlsPanel = new JPanel();
        miscControlsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 2));
        miscControlsPanel.setBackground(bg);
        miscControlsPanel.add(createIconButton(aboutAction, bg));
        toolBar.add(miscControlsPanel);

        // rootComponent
        final JSplitPane leftComponent = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, true, currentLevelPanel,
                reactionsPane);
        leftComponent.setOneTouchExpandable(true);
        final JSplitPane rootComponent = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, true, leftComponent,
                collisionsPanel);
        rootComponent.setOneTouchExpandable(true);
        // contentpane
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(rootComponent, BorderLayout.CENTER);

        frame.setContentPane(contentPane);
        SwingUtilities.updateComponentTreeUI(frame);
        frame.setVisible(true);
    }

    public GUI(final MessageSource messageSource,
            final ApplicationEngine applicationEngine, final Action aboutAction, final LevelNavigatorView levelNavigatorView, final StateView stateView, final ReactionEditorView reactionEditorView, final AtomsView atomsView, final ImageIcon parameterIcon) {
        GUI.messageSource = messageSource;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI(applicationEngine, aboutAction, levelNavigatorView, stateView, reactionEditorView, atomsView, parameterIcon);
            }
        });
    }

    private static JButton createIconButton(final Action action, final Color bg) {
        final JButton button = new JButton(action);
        button.setMargin(new Insets(-3, -3, -3, -3));
        button.setBorderPainted(false);
        button.setBackground(bg);
        return button;
    }

    private static Action createParametersAction(final JPanel p1,
            final JPanel p2, final JPanel p3, final ImageIcon parameterIcon) {
        final JPanel message = new JPanel();
        message.setLayout(new BoxLayout(message, BoxLayout.PAGE_AXIS));
        message.add(p1);
        message.add(p2);
        message.add(p3);
        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                JOptionPane.showMessageDialog(null, message,
                        GUI.localize("application.parameters"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                GUI.localize("application.parameters"));
        action.putValue(Action.SMALL_ICON, parameterIcon);
        return action;
    }

    public static String localize(final String key) {
        return GUI.messageSource.getMessage(key, null, Locale.getDefault());
    }

}
