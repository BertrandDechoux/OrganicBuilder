package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.EngineDispatcher;
import uk.org.squirm3.engine.IApplicationEngine;

public class GUI {
	
	public static void createGUI(final IApplicationEngine iApplicationEngine, final JApplet applet) {
		Resource.loadPictures();
		final EngineDispatcher engineDispatcher = new EngineDispatcher();
		iApplicationEngine.addListener(engineDispatcher);
		
			//frame
		JFrame frame = new JFrame(Application.localize(new String[] {"interface","application","title"}));
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize((int)screenSize.getWidth(),(int)(0.9*screenSize.getHeight()));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					iApplicationEngine.removeListener(engineDispatcher);
				}
				public void windowDeiconified(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
			});

			//listeners
		AtomsListener atomsListener = new AtomsListener(iApplicationEngine);
		CurrentLevelListener currentLevelListener = new CurrentLevelListener(iApplicationEngine);
		ReactionsListener reactionsListener = new ReactionsListener(iApplicationEngine);
		final SimulationListener simulationListener = new SimulationListener(iApplicationEngine);
		LevelNavigator levelNavigator = new LevelNavigator(iApplicationEngine);
				//ajout
		engineDispatcher.addListener(atomsListener);
		engineDispatcher.addListener(currentLevelListener);
		engineDispatcher.addListener(reactionsListener);
		engineDispatcher.addListener(simulationListener);
		
			//main panels
		JPanel collisionsPanel 		= atomsListener.getCollisionsPanel();
		JPanel currentLevelPanel	= currentLevelListener.getCurrentLevelPanel();
		JPanel reactionsPanel		= reactionsListener.getReactionsPanel();
		
			//toolbar
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new BoxLayout(toolBar,BoxLayout.X_AXIS));
		Color bg = new Color(255,255,225);
		toolBar.setBackground(bg);
			// simulation controls
			JPanel simControlsPanel = new JPanel();
			simControlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT,1,2));
			simControlsPanel.setBackground(bg);
			simControlsPanel.add(createIconButton(simulationListener.getStopAction(),bg));
			simControlsPanel.add(createIconButton(simulationListener.getRunAction(),bg));
			simControlsPanel.add(createIconButton(simulationListener.getResetAction(),bg));
			simControlsPanel.add(createIconButton(createParametersAction(simulationListener.getParametersPanel()),bg));
		toolBar.add(simControlsPanel);
		toolBar.add(Box.createHorizontalGlue());
			// navigation controls
			JPanel navControlsPanel = new JPanel();
			navControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER,1,2));
			navControlsPanel.setBackground(bg);
			navControlsPanel.add(createIconButton(levelNavigator.getIntroAction(),bg));
			navControlsPanel.add(createIconButton(levelNavigator.getPreviousAction(),bg));
			JComboBox cb = levelNavigator.getLevelComboBox();
			cb.setMaximumSize(new Dimension(150,80));
			navControlsPanel.add(cb);
			navControlsPanel.add(createIconButton(levelNavigator.getNextAction(),bg));
			navControlsPanel.add(createIconButton(levelNavigator.getLastAction(),bg));
		toolBar.add(navControlsPanel);
		toolBar.add(Box.createHorizontalGlue());
			//misc controls
			JPanel miscControlsPanel = new JPanel();
			miscControlsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,1,2));
			miscControlsPanel.setBackground(bg);
			miscControlsPanel.add(createIconButton(createAboutAction(),bg));
			if(applet!=null) miscControlsPanel.add(createIconButton(createAppletAction(frame,applet),bg));
		toolBar.add(miscControlsPanel);
		
		// rootComponent
		JSplitPane leftComponent = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, currentLevelPanel, reactionsPanel);
		leftComponent.setOneTouchExpandable(true);
		JSplitPane rootComponent = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftComponent, collisionsPanel);
		rootComponent.setOneTouchExpandable(true);
			// contentpane
		final JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(toolBar, BorderLayout.NORTH);
		contentPane.add(rootComponent, BorderLayout.CENTER);
		
		levelNavigator.init();
		if(applet==null) {
			frame.setContentPane(contentPane);
			frame.setVisible(true);
		} else {
			applet.setContentPane(contentPane);
		}
		
	}
	
	private static JButton createIconButton(Action action, Color bg) {
		final JButton button = new JButton(action);
		button.setMargin(new Insets(-3,-3,-3,-3));
		button.setBorderPainted(false);
		button.setBackground(bg);
		return button;
	}
	
	private static Action createParametersAction(final JPanel p) {
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, p,
						Application.localize(new String[] {"interface","application","parameters"}),
						JOptionPane.INFORMATION_MESSAGE);
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","application","parameters"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("parameters"));
	    return action;
	}
	
	private static Action createAboutAction() {	//TODO mise en page des textes
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(Application.localize(new String[] {"interface","application","about"}),
				null, createScrolledTA("about.txt"), null);
		tabbedPane.addTab(Application.localize(new String[] {"interface","application","license"}),
				null, createScrolledTA("license.txt"), null);
		tabbedPane.addTab(Application.localize(new String[] {"interface","application","explanation"}),
				null, createScrolledTA("explanation.txt"), null);
		//tabbedPane.setMnemonicAt(0, KeyEvent.VK_1); TODO

		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, tabbedPane,
						Application.localize(new String[] {"interface","application","about"}),
						JOptionPane.QUESTION_MESSAGE);
			}
	    };
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","application","about"}));
	    action.putValue(Action.SMALL_ICON, Resource.getIcon("about"));
	    return action;
	}
	
	private static Action createAppletAction(final JFrame frame, final JApplet applet) { //TODO externalize strings
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(frame.isVisible()) {
					frame.setVisible(false);
					putValue(Action.NAME,"Undock");
					putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","application","about"}));
					applet.setContentPane(frame.getContentPane());
					applet.repaint();
				} else {
					putValue(Action.NAME,"Dock");
					putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","application","about"}));
					frame.setContentPane(applet.getContentPane());
					frame.setVisible(true);
					applet.repaint();
				}
			}
	    };
	    action.putValue(Action.NAME,"Undock");
	    action.putValue(Action.SHORT_DESCRIPTION, Application.localize(new String[] {"interface","application","about"}));
	    return action;
	}
	
	private static JScrollPane createScrolledTA(String fileName) {
		JTextArea message  = new JTextArea(Resource.getFileContent(fileName));
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setEditable(false);
		JScrollPane sp = new JScrollPane(message);
		sp.setPreferredSize(new Dimension(600,400));
		return sp;
	}

}
