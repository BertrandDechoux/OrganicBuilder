package uk.org.squirm3;

import javax.swing.JApplet;

public class Applet extends JApplet {
	
	public void init() {
		new Application(this);
	}

}
