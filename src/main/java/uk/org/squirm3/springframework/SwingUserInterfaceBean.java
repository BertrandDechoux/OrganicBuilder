package uk.org.squirm3.springframework;

import javax.swing.SwingUtilities;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import uk.org.squirm3.config.GuiConfig;

public class SwingUserInterfaceBean implements ApplicationContextAware {
    @Override
    public void setApplicationContext(final ApplicationContext parent) throws BeansException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
                context.setParent(parent);
                context.register(GuiConfig.class);
                context.refresh();
            }
        });
    }
}
