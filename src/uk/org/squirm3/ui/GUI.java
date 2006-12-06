package uk.org.squirm3.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import uk.org.squirm3.Application;
import uk.org.squirm3.engine.EngineDispatcher;
import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.IEngineListener;

public class GUI {
	private final JFrame frame;
	
	private final EngineDispatcher engineDispatcher;
	private final IApplicationEngine iApplicationEngine;
	
	public GUI(final IApplicationEngine iApplicationEngine, Container contentPane) {
		frame = new JFrame(Application.localize(new String[] {"interface","application","title"}));
			// applictionEngine
		engineDispatcher = new EngineDispatcher();
		this.iApplicationEngine = iApplicationEngine;
		iApplicationEngine.addListener(engineDispatcher);	
			//show frame
		frame.setContentPane(contentPane);
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
		frame.setVisible(true);
	}
	
	public void addListener(IEngineListener l) {
		engineDispatcher.addListener(l);
	}
	
	public static GUI createGUI(IApplicationEngine iApplicationEngine) {
		Resource.loadPictures();
			//listeners
		AtomsListener atomsListener = new AtomsListener(iApplicationEngine);
		CurrentLevelListener currentLevelListener = new CurrentLevelListener(iApplicationEngine);
		ReactionsListener reactionsListener = new ReactionsListener(iApplicationEngine);
		final SimulationListener simulationListener = new SimulationListener(iApplicationEngine);
		LevelNavigator levelNavigator = new LevelNavigator(iApplicationEngine);
		
			//main panels
		JPanel collisionsPanel 		= atomsListener.getCollisionsPanel();
		JPanel currentLevelPanel	= currentLevelListener.getCurrentLevelPanel();
		JPanel reactionsPanel		= reactionsListener.getReactionsPanel();
		
			//toolbar
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		Color bg = new Color(255,255,225);
		toolBar.setBackground(bg);
				// simulation controls
		toolBar.add(createIconButton(simulationListener.getStopAction(),bg));
		toolBar.add(createIconButton(simulationListener.getRunAction(),bg));
		toolBar.add(createIconButton(simulationListener.getResetAction(),bg));
		toolBar.addSeparator();
					//parameters
		toolBar.add(createIconButton(createParametersAction(simulationListener.getParametersPanel()),bg));
		toolBar.addSeparator(new Dimension(50,10));
			// navigation controls
		toolBar.add(createIconButton(levelNavigator.getIntroAction(),bg));
		toolBar.add(createIconButton(levelNavigator.getPreviousAction(),bg));
		JComboBox cb = levelNavigator.getLevelComboBox();
		cb.setMaximumSize(new Dimension(150,80));
		toolBar.add(cb);
		toolBar.add(createIconButton(levelNavigator.getNextAction(),bg));
		toolBar.add(createIconButton(levelNavigator.getLastAction(),bg));
		toolBar.addSeparator();
			//about control
		toolBar.add(createIconButton(createAboutAction(),bg));
		
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
		
			//gui
		final GUI gui = new GUI(iApplicationEngine, contentPane);
		gui.addListener(atomsListener);
		gui.addListener(currentLevelListener);
		gui.addListener(reactionsListener);
		gui.addListener(simulationListener);
		
		levelNavigator.init();
		
		return gui;
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
	
	private static Action createAboutAction() {	//TODO
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
