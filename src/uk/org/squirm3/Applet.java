package uk.org.squirm3;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

public class Applet extends JApplet {
	
	public void init() {
		new Application(this);
	}

}
