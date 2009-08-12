package uk.org.squirm3;

import javax.swing.*;
import java.util.Locale;

/**
 * ${my.copyright}
 */

@SuppressWarnings("serial")
public final class Applet extends JApplet {

    @Override
    public String getAppletInfo() {
        assert (super.getAppletInfo() == null);
        return null; // TODO add information
    }

    @Override
    public String[][] getParameterInfo() {
        assert (super.getParameterInfo() == null);
        return new String[][]{
                {Application.USER_LANGUAGE, "ISO-639", "your language"},
                {Application.USER_REGION, "ISO-3166", "your region"},
        };
    }

    @Override
    public void init() {
        super.init();
        try {
            String userLanguage = getParameter(Application.USER_LANGUAGE);
            String userRegion = getParameter(Application.USER_REGION);
            Locale userLocale = (userLanguage == null) ? null :
                    new Locale(userLanguage, userRegion);
            // TODO use the locale and do not pass the applet instance
            Application.runAsApplet(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e, "Exception",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

}
