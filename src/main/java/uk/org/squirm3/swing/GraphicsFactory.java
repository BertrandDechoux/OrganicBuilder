package uk.org.squirm3.swing;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class GraphicsFactory {
    private final Component component = new JFrame();

    public Image createImage(final String imagePath)
            throws InterruptedException {
        final URL url = GraphicsFactory.class.getResource(imagePath);
        final Image image = Toolkit.getDefaultToolkit().createImage(url);
        // XXX Not the most efficient but sufficient for now
        final MediaTracker tracker = new MediaTracker(component);
        tracker.addImage(image, 0);
        tracker.waitForID(0);

        return image;
    }

    public Icon createIcon(final String imagePath) throws InterruptedException {
        if (imagePath.endsWith(".svg")) {
            return createSvgIcon(imagePath);
        }
        return new ImageIcon(createImage(imagePath));
    }

    /**
     * Vector icons scale with the UI scale factor and are recolored to the
     * current label foreground, so they follow theme changes.
     */
    private Icon createSvgIcon(final String imagePath) {
        // ClassLoader.getResource does not accept a leading slash
        final String resource = imagePath.startsWith("/")
                ? imagePath.substring(1)
                : imagePath;
        final FlatSVGIcon icon = new FlatSVGIcon(resource, 20, 20,
                getClass().getClassLoader());
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(
                color -> UIManager.getColor("Label.foreground")));
        return icon;
    }
}
