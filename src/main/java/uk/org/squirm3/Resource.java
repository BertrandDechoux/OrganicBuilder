package uk.org.squirm3;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

// *********************************************************
// class Resource : use to get pictures or to log something
// all thingd related to I/O
// *********************************************************
// TODO clean all the code loading the images
public final class Resource {

    /**
     * This is an utility class. Do no instantiate.
     */
    private Resource() {
    }

    private static final String spikyImageName = "spiky.png";
    private static Image spikyImage;

    private static BufferedImage loadCompatibleImage(final String name)
            throws IOException {
        final URL url = Resource.class.getResource("/graphics/" + name);
        final BufferedImage image = ImageIO.read(url);
        final GraphicsConfiguration configuration = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration();
        final BufferedImage compatibleImage = configuration
                .createCompatibleImage(image.getWidth(), image.getHeight(),
                        Transparency.BITMASK);
        // BufferedImage compatibleImage =
        // configuration.createCompatibleImage(image.getWidth(),
        // image.getHeight(), Transparency.TRANSLUCENT);
        final Graphics g = compatibleImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return compatibleImage;
    }

    public static void loadPictures() { // TODO find a better way
        // listener
        final JFrame f = new JFrame();
        spikyImage = loadImage(spikyImageName, f);
    }

    // used to load an image from the jar into an Image structure
    private static Image loadImage(final String name, final Component c) {
        final boolean old_version = true;
        if (old_version) { // TODO make a benchmark or something
            // in order to chose the most efficent method
            final URL url = Resource.class.getResource("/graphics/" + name);
            final Image img = c.getToolkit().createImage(url);
            try {
                final MediaTracker tracker = new MediaTracker(c);
                tracker.addImage(img, 0);
                tracker.waitForID(0);
            } catch (final Exception e) {
            }
            return img;
        } else {
            try {
                return loadCompatibleImage(name);
            } catch (final Exception e) {
            }
        }
        return null;
    }

    // get the image
    public static Image getSpikyImage() {
        return spikyImage;
    }

    public static Icon getIcon(final String name) {
        final URL url = Resource.class
                .getResource("/graphics/" + name + ".png");
        return new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
    }

    // return the content of the file
    public static String getFileContent(final String fileName) {
        final StringBuffer sb = new StringBuffer();
        InputStream is;
        try {
            is = Resource.class.getResourceAsStream("/" + fileName);
            int read;
            while ((read = is.read()) != -1) {
                sb.append((char) read);
            }
            is.close();
        } catch (final Exception e) {
            System.out.println("error : " + e);
            e.printStackTrace();
        }
        if (sb != null) {
            return sb.toString();
        }
        return "ERROR : " + fileName;
    }
}
