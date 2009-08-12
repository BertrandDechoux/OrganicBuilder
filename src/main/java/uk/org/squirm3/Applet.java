package uk.org.squirm3;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

/**
${my.copyright}
 */

@SuppressWarnings("serial")
public final class Applet extends JApplet {

    /**
     * Called by the browser or applet viewer
     * to inform this applet that it has been loaded
     *  into the system.
     */
    @Override
    public void init() {
        super.init();
        try {
            Application.runAsApplet(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e, e.getClass().toString(),
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}
