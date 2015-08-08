package uk.org.squirm3.springframework;

import javax.swing.SwingUtilities;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SwingUserInterfaceBean implements ApplicationContextAware {

    public static final String DEFAULT_GUI_CONTEXT = "swing-context.xml";

    @Override
    public void setApplicationContext(final ApplicationContext parent) throws BeansException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] configLocations = new String[] { DEFAULT_GUI_CONTEXT };
                new ClassPathXmlApplicationContext(configLocations, parent);
            }
        });
    }

}
