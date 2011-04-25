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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.springframework.context.MessageSource;

import uk.org.squirm3.Resource;
import uk.org.squirm3.engine.ApplicationEngine;

public class GUI {
    
    private static MessageSource messageSource;

    public static void createGUI(final ApplicationEngine applicationEngine) {

        Resource.loadPictures();

        // frame
        final JFrame frame = new JFrame(
                GUI.localize("application.title"));
        frame.setSize(1080, 630);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // view
        final AtomsView atomsView = new AtomsView(applicationEngine);
        final CurrentLevelView currentLevelView = new CurrentLevelView(
                applicationEngine);
        final ReactionListView reactionListView = new ReactionListView(
                applicationEngine);
        final ReactionEditorView reactionEditorView = new ReactionEditorView(
                applicationEngine);
        final StateView stateView = new StateView(applicationEngine);
        final LevelNavigatorView levelNavigatorView = new LevelNavigatorView(
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
                                atomsView.getControlsPanel()), bg));
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
        miscControlsPanel.add(createIconButton(createAboutAction(), bg));
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

    public static String selectLanguage(final String[] languages) {
        final Icon[] icons = new Icon[languages.length];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = Resource.getIcon(languages[i]);
        }
        final int choice = JOptionPane.showOptionDialog(null, "",
                "Organic Builder", JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, icons, null);
        if (choice == JOptionPane.CLOSED_OPTION) {
            return null;
        } else {
            return languages[choice];
        }
    }

    public GUI(final MessageSource messageSource, final ApplicationEngine applicationEngine) {
        GUI.messageSource = messageSource;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI(applicationEngine);
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
            final JPanel p2, final JPanel p3) {
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

            public void actionPerformed(final ActionEvent e) {
                JOptionPane.showMessageDialog(null, message,
                        GUI.localize("application.parameters"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                GUI.localize("application.parameters"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("parameters"));
        return action;
    }

    private static Action createAboutAction() { // TODO mise en page des textes
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(GUI.localize("application.about"), null,
                createTextPane("about.html"), null);
        tabbedPane.addTab(GUI.localize("application.license"), null,
                createTextPane("license.html"), null);

        final Action action = new AbstractAction() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                JOptionPane.showMessageDialog(null, tabbedPane,
                        GUI.localize("application.about"),
                        JOptionPane.QUESTION_MESSAGE);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION,
                GUI.localize("application.about"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("about"));
        return action;
    }

    private static JScrollPane createTextPane(final String fileName) {
        final JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        textPane.setText(Resource.getFileContent(fileName));
        textPane.setEditable(false);
        final JScrollPane sp = new JScrollPane(textPane);
        sp.setPreferredSize(new Dimension(600, 400));
        return sp;
    }

    public static String localize(final String key) {
        return GUI.messageSource.getMessage(key, null, Locale.getDefault());
    }

}
