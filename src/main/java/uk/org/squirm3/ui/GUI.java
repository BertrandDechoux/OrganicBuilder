package uk.org.squirm3.ui;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.ApplicationEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ${my.copyright}
 */

public class GUI {

    public static void createGUI(final ApplicationEngine applicationEngine, final JRootPane rootPane) {
        // create the graphical interface
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI(applicationEngine, rootPane);
            }
        });
    }

    public static String selectLanguage(String[] languages) {
        Icon[] icons = new Icon[languages.length];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = Resource.getIcon(languages[i]);
        }
        final int choice = JOptionPane.showOptionDialog(null,
                "", "Organic Builder", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, icons, null);
        if (choice == JOptionPane.CLOSED_OPTION) return null;
        else return languages[choice];
    }

    public GUI(final ApplicationEngine applicationEngine, final JRootPane rootPane) {
        Resource.loadPictures();

        //frame
        JFrame frame = new JFrame(Application.localize("application.title"));
        frame.setSize(1080, 630);
        if (rootPane == null) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }

        //view
        AtomsView atomsView = new AtomsView(applicationEngine);
        CurrentLevelView currentLevelView = new CurrentLevelView(applicationEngine,
                Application.getConfigurationProperty("logger.url"));
        ReactionListView reactionListView = new ReactionListView(applicationEngine);
        ReactionEditorView reactionEditorView = new ReactionEditorView(applicationEngine);
        StateView stateView = new StateView(applicationEngine);
        LevelNavigatorView levelNavigatorView = new LevelNavigatorView(applicationEngine);
        CustomResetView customResetView = new CustomResetView(applicationEngine);
        SpeedView speedView = new SpeedView(applicationEngine);

        //main panels
        JComponent collisionsPanel = atomsView.getCollisionsPanel();
        JPanel currentLevelPanel = currentLevelView.getCurrentLevelPanel();
        JPanel reactionEditorPanel = reactionEditorView.getEditorPanel();
        reactionEditorPanel.setMaximumSize(reactionEditorPanel.getMinimumSize());

        final JSplitPane reactionsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, reactionEditorView.getEditorPanel(), reactionListView.getListPanel());
        reactionsPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent arg0) {
                if (arg0.getPropertyName().equals("dividerLocation")
                        && !arg0.getNewValue().toString().equals("1")) {
                    reactionsPane.setDividerLocation(-1);
                }
            }
        });
        reactionsPane.setOneTouchExpandable(true);

        //toolbar
        JPanel toolBar = new JPanel();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        Color bg = new Color(255, 255, 225);
        toolBar.setBackground(bg);
        // simulation controls
        JPanel simControlsPanel = new JPanel();
        simControlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 2));
        simControlsPanel.setBackground(bg);
        simControlsPanel.add(createIconButton(stateView.getStopAction(), bg));
        simControlsPanel.add(createIconButton(stateView.getRunAction(), bg));
        simControlsPanel.add(createIconButton(stateView.getResetAction(), bg));
        simControlsPanel.add(createIconButton(createParametersAction(customResetView.getPanel(), speedView.getPanel(), atomsView.getControlsPanel()), bg));
        toolBar.add(simControlsPanel);
        toolBar.add(Box.createHorizontalGlue());
        // navigation controls
        JPanel navControlsPanel = new JPanel();
        navControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
        navControlsPanel.setBackground(bg);
        navControlsPanel.add(createIconButton(levelNavigatorView.getIntroAction(), bg));
        navControlsPanel.add(createIconButton(levelNavigatorView.getPreviousAction(), bg));
        JComboBox cb = levelNavigatorView.getLevelComboBox();
        cb.setMaximumSize(new Dimension(150, 80));
        navControlsPanel.add(cb);
        navControlsPanel.add(createIconButton(levelNavigatorView.getNextAction(), bg));
        navControlsPanel.add(createIconButton(levelNavigatorView.getLastAction(), bg));
        toolBar.add(navControlsPanel);
        toolBar.add(Box.createHorizontalGlue());
        //misc controls
        JPanel miscControlsPanel = new JPanel();
        miscControlsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 2));
        miscControlsPanel.setBackground(bg);
        miscControlsPanel.add(createIconButton(createAboutAction(), bg));
        if (rootPane != null)
            miscControlsPanel.add(createIconButton(createDockingAction(frame, rootPane), bg));
        toolBar.add(miscControlsPanel);

        // rootComponent
        JSplitPane leftComponent = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, currentLevelPanel, reactionsPane);
        leftComponent.setOneTouchExpandable(true);
        JSplitPane rootComponent = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftComponent, collisionsPanel);
        rootComponent.setOneTouchExpandable(true);
        // contentpane
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(rootComponent, BorderLayout.CENTER);

        if (rootPane == null) {
            frame.setContentPane(contentPane);
            SwingUtilities.updateComponentTreeUI(frame);
            frame.setVisible(true);
        } else {
            rootPane.setContentPane(contentPane);
            SwingUtilities.updateComponentTreeUI(rootPane);
        }

    }


    private static JButton createIconButton(Action action, Color bg) {
        final JButton button = new JButton(action);
        button.setMargin(new Insets(-3, -3, -3, -3));
        button.setBorderPainted(false);
        button.setBackground(bg);
        return button;
    }

    private static Action createParametersAction(final JPanel p1, final JPanel p2, final JPanel p3) {
        final JPanel message = new JPanel();
        message.setLayout(new BoxLayout(message, BoxLayout.PAGE_AXIS));
        message.add(p1);
        message.add(p2);
        message.add(p3);
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, message,
                        Application.localize("application.parameters"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize("application.parameters"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("parameters"));
        return action;
    }

    private static Action createAboutAction() {    //TODO mise en page des textes
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(Application.localize("application.about"),
                null, createTextPane("about.html"), null);
        tabbedPane.addTab(Application.localize("application.license"),
                null, createTextPane("license.html"), null);

        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, tabbedPane,
                        Application.localize("application.about"),
                        JOptionPane.QUESTION_MESSAGE);
            }
        };
        action.putValue(Action.SHORT_DESCRIPTION, Application.localize("application.about"));
        action.putValue(Action.SMALL_ICON, Resource.getIcon("about"));
        return action;
    }

    private static Action createDockingAction(final JFrame frame, final JRootPane rootPane) {
        final Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (frame.isVisible()) {
                    frame.setVisible(false);
                    putValue(Action.NAME, Application.localize("undock"));
                    rootPane.setContentPane(frame.getContentPane());
                    SwingUtilities.updateComponentTreeUI(rootPane);
                } else {
                    putValue(Action.NAME, Application.localize("dock"));
                    frame.setContentPane(rootPane.getContentPane());
                    frame.setVisible(true);
                    SwingUtilities.updateComponentTreeUI(frame);
                     SwingUtilities.updateComponentTreeUI(rootPane);
                }
            }
        };
        action.putValue(Action.NAME, Application.localize("undock"));
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        action.putValue(Action.NAME, Application.localize("undock"));
                        rootPane.setContentPane(frame.getContentPane());
                        SwingUtilities.updateComponentTreeUI(rootPane);
                    }

                    public void windowDeiconified(WindowEvent e) {
                    }

                    public void windowIconified(WindowEvent e) {
                    }
                });
        return action;
    }

    private static JScrollPane createTextPane(String fileName) {
        JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        textPane.setText(Resource.getFileContent(fileName));
        textPane.setEditable(false);
        JScrollPane sp = new JScrollPane(textPane);
        sp.setPreferredSize(new Dimension(600, 400));
        return sp;
    }

}
