package uk.org.squirm3;

import javax.swing.JApplet;

public class Applet extends JApplet {
	private static final String language = "language";
	
	public void init() {
		new Application(this,getParameter(language));
	}

}
