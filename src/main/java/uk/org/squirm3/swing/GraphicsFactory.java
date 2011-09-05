package uk.org.squirm3.swing;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

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
        return new ImageIcon(createImage(imagePath));
    }
}
