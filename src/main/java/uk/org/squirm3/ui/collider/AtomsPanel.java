package uk.org.squirm3.ui.collider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import uk.org.squirm3.derivative.RoundGradientPaint;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.DraggingPoint;

public class AtomsPanel extends JScrollPane {
    private static final long serialVersionUID = 1L;

    DraggingPoint draggingPoint;
    Atom[] latestAtomsCopy;

    private int simulationWidth;
    private int simulationHeight;

    BufferedImage bimg;
    private boolean needRepaint = true;
    byte scale;

    private JPanel imagePanel;

    // yellow, grey, blue, purple, red, green
    private static final Color atomsColors[] = {new Color(0xbdcf00),
            new Color(0x5f5f5f), new Color(0x0773db), new Color(0xee10ac),
            new Color(0xef160f), new Color(0x00df06)};
    private static final BufferedImage[] atomsImages = new BufferedImage[atomsColors.length];
    private final Image spikyImage;

    final ApplicationEngine applicationEngine;

    public AtomsPanel(final ApplicationEngine applicationEngine,
            final Image spikyImage) {
        imagePanel = new ImagePanel(this);
        setViewportView(imagePanel);

        this.applicationEngine = applicationEngine;
        createAtomsImages();
        this.spikyImage = spikyImage;
        needRepaint = true;
        scale = 100;
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(final ComponentEvent arg0) {
                imageSizeHasChanged();
            }

            @Override
            public void componentHidden(final ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(final ComponentEvent arg0) {
            }

            @Override
            public void componentShown(final ComponentEvent arg0) {
            }
        });

        final Configuration configuration = applicationEngine
                .getConfiguration();
        simulationHeight = (int) configuration.getHeight();
        simulationWidth = (int) configuration.getWidth();
        imageSizeHasChanged();

        final IListener atomsListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                final Collection<? extends Atom> c = applicationEngine
                        .getAtoms();
                final Iterator<? extends Atom> it = c.iterator();
                latestAtomsCopy = new Atom[c.size()];
                int i = 0;
                while (it.hasNext()) {
                    latestAtomsCopy[i] = it.next();
                    i++;
                }
                imageHasChanged();
            }
        };
        atomsListener.propertyHasChanged();
        applicationEngine.getEventDispatcher().addListener(atomsListener,
                EventDispatcher.Event.ATOMS);

        final IListener draggingPointListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                draggingPoint = applicationEngine.getCurrentDraggingPoint();
                imageHasChanged();
            }
        };
        draggingPointListener.propertyHasChanged();
        applicationEngine.getEventDispatcher().addListener(
                draggingPointListener, EventDispatcher.Event.DRAGGING_POINT);

    }

    private static void createAtomsImages() {
        // size
        final float R = Atom.getAtomSize() - 2;
        final int w = (int) (2 * R);
        final int h = (int) (2 * R);
        for (int i = 0; i < atomsColors.length; i++) {
            // creation of the image
            atomsImages[i] = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_ARGB);
            // creation of the graphic
            final Graphics2D g2 = atomsImages[i].createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            // creation of the colors
            final Color baseColor = atomsColors[i];
            final int colorOffset = 220;
            final int red = baseColor.getRed() < 255 - colorOffset ? baseColor
                    .getRed() + colorOffset : 255;
            final int green = baseColor.getGreen() < 255 - colorOffset
                    ? baseColor.getGreen() + colorOffset
                    : 255;
            final int blue = baseColor.getBlue() < 255 - colorOffset
                    ? baseColor.getBlue() + colorOffset
                    : 255;
            final Color lightColor = new Color(red, green, blue);
            // drawing the image
            final RoundGradientPaint gradient = new RoundGradientPaint(w / 3,
                    h / 3, lightColor, new Point2D.Double(w / 2, h / 2),
                    baseColor);
            g2.setPaint(gradient);
            g2.fillOval(0, 0, w, h);
        }
    }

    private void imageSizeHasChanged() {
        if (imagePanel != null) {
            final Dimension d = getSize();
            final int pseudoScale = (int) (Math.min(
                    (d.height * 0.99 / simulationHeight),
                    (d.width * 0.99 / simulationWidth)) * 100);
            scale = pseudoScale >= 100 ? 100 : (byte) pseudoScale;
        }
        final float zoom = (float) scale / 100;
        imagePanel
                .setPreferredSize(new Dimension((int) (simulationWidth * zoom),
                        (int) (simulationHeight * zoom)));
        SwingUtilities.updateComponentTreeUI(this);
        imageHasChanged();
    }

    private void imageHasChanged() {
        needRepaint = true;
        if (imagePanel == null || !imagePanel.isDisplayable()) {
            return;
        }
        imagePanel.repaint();
    }

    void updateImage() {
        if (!needRepaint) {
            return;
        }
        needRepaint = false;
        final int R = (int) Atom.getAtomSize();
        // to avoid the array to be changed (multithreading issue maybe later)
        final Atom[] atoms = latestAtomsCopy;

        // get the dimensions
        final int w = simulationWidth;
        final int h = simulationHeight;

        // do we have a correct bimg ?
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        if (bimg == null) {
            return;// collisionsPanel is not displayable
        }

        // create graphics
        final Graphics2D g2 = bimg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, w, h);

        // draw the atoms themselves
        g2.setStroke(new BasicStroke(4));
        g2.setFont(new Font("Arial", Font.BOLD, R));
        g2.setPaint(Color.black);
        if (atoms != null) {
            final int offset_x = R;
            final int offset_y = R;
            final int text_offset_y = (int) (R * 8.0 / 22.0);
            for (int i = 0; i < atoms.length; i++) {
                if (!atoms[i].isKiller()) {
                    // draw the normal colour atom image and label it
                    g2.drawImage(atomsImages[atoms[i].getType()],
                            (int) atoms[i].getPhysicalPoint().getPositionX()
                                    - offset_x, (int) atoms[i]
                                    .getPhysicalPoint().getPositionY()
                                    - offset_y, R * 2, R * 2, this);
                    final String label = atoms[i].toString();
                    final int width = g2.getFontMetrics().stringWidth(label);
                    g2.drawString(label, (int) atoms[i].getPhysicalPoint()
                            .getPositionX() - width / 2, (int) atoms[i]
                            .getPhysicalPoint().getPositionY() + text_offset_y);
                } else {
                    // draw a special spiky image and no label
                    g2.drawImage(spikyImage, (int) atoms[i].getPhysicalPoint()
                            .getPositionX() - offset_x, (int) atoms[i]
                            .getPhysicalPoint().getPositionY() - offset_y,
                            R * 2, R * 2, this);
                }
            }
        }

        // draw the bonds
        g2.setPaint(new Color(0, 0, 0, 50));
        if (atoms != null) {
            for (final Atom atom : atoms) {
                final Iterator<Atom> it = atom.getBonds().iterator();
                while (it.hasNext()) {
                    final Atom other = it.next();
                    final float x1 = atom.getPhysicalPoint().getPositionX();
                    final float y1 = atom.getPhysicalPoint().getPositionY();
                    final float dx = other.getPhysicalPoint().getPositionX()
                            - x1;
                    final float dy = other.getPhysicalPoint().getPositionY()
                            - y1;
                    final float d = (float) Math.sqrt(dx * dx + dy * dy);
                    final float x_cut = dx * R * 0.8f / d;
                    final float y_cut = dy * R * 0.8f / d;
                    g2.drawLine((int) (x1 + x_cut), (int) (y1 + y_cut),
                            (int) (x1 + dx - x_cut), (int) (y1 + dy - y_cut));
                }
            }
        }

        // draw the dragging line if currently dragging
        if (draggingPoint != null) {
            g2.setStroke(new BasicStroke(5));
            g2.setPaint(new Color(0, 0, 0, 100));
            g2.drawLine((int) draggingPoint.getX(), (int) draggingPoint.getY(),
                    (int) atoms[draggingPoint.getWhichBeingDragging()]
                            .getPhysicalPoint().getPositionX(),
                    (int) atoms[draggingPoint.getWhichBeingDragging()]
                            .getPhysicalPoint().getPositionY());
            g2.setStroke(new BasicStroke(4)); // else the stroke would have been
                                              // changed
            // when outlining the collider area
        }

        // draw the dragging point used
        if (applicationEngine.getLastUsedDraggingPoint() != null) {
            final DraggingPoint lastUsedDraggingPoint = applicationEngine
                    .getLastUsedDraggingPoint();
            g2.setStroke(new BasicStroke(1));
            g2.setPaint(new Color(200, 0, 0, 100));
            g2.drawLine((int) lastUsedDraggingPoint.getX(),
                    (int) lastUsedDraggingPoint.getY(),
                    (int) atoms[lastUsedDraggingPoint.getWhichBeingDragging()]
                            .getPhysicalPoint().getPositionX(),
                    (int) atoms[lastUsedDraggingPoint.getWhichBeingDragging()]
                            .getPhysicalPoint().getPositionY());
            g2.setStroke(new BasicStroke(4)); // else the stroke would have been
                                              // changed
            // when outlining the collider area
        }

        // outline the collider area
        g2.setPaint(new Color(100, 100, 200));
        g2.drawRoundRect(2, 1, simulationWidth - 4, simulationHeight - 4, 9, 9);
        g2.dispose();
    }

}
