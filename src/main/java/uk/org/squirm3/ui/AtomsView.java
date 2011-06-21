package uk.org.squirm3.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.org.squirm3.Resource;
import uk.org.squirm3.derivative.RoundGradientPaint;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.IListener;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.DraggingPoint;

public class AtomsView extends AView {
    private DraggingPoint draggingPoint;
    private Atom[] latestAtomsCopy;

    private int simulationWidth;
    private int simulationHeight;

    private BufferedImage bimg;
    private boolean needRepaint = true;
    private byte scale;

    private JCheckBox auto;
    private JSlider scaleSlider;

    private JPanel imagePanel;
    private JPanel controlsPanel;

    private final JComponent collisionsPanel;
    private JScrollPane scrollPane;

    // yellow, grey, blue, purple, red, green
    private static final Color atomsColors[] = {new Color(0xbdcf00),
            new Color(0x5f5f5f), new Color(0x0773db), new Color(0xee10ac),
            new Color(0xef160f), new Color(0x00df06)};
    private static final BufferedImage[] atomsImages = new BufferedImage[atomsColors.length];

    public AtomsView(final ApplicationEngine applicationEngine) {
        super(applicationEngine);
        createAtomsImages();
        needRepaint = true;
        scale = 100;
        collisionsPanel = createCollisionsPanel();

        final IListener sizeListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                final Configuration configuration = getApplicationEngine()
                        .getLevelManager().getCurrentLevel().getConfiguration();
                simulationHeight = (int) configuration.getHeight();
                simulationWidth = (int) configuration.getWidth();
                imageSizeHasChanged();
            }
        };
        sizeListener.propertyHasChanged();
        applicationEngine.getEventDispatcher().addListener(sizeListener,
                EventDispatcher.Event.LEVEL);
        applicationEngine.getEventDispatcher().addListener(sizeListener,
                EventDispatcher.Event.CONFIGURATION);

        final IListener atomsListener = new IListener() {
            @Override
            public void propertyHasChanged() {
                final Collection<? extends Atom> c = getApplicationEngine()
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
                draggingPoint = getApplicationEngine()
                        .getCurrentDraggingPoint();
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

    public JComponent getCollisionsPanel() {
        return collisionsPanel;
    }

    private JComponent createCollisionsPanel() {
        imagePanel = new ImagePanel();
        scrollPane = new JScrollPane(imagePanel);
        scrollPane.addComponentListener(new ComponentListener() {
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

        controlsPanel = new JPanel();
        controlsPanel.add(new JLabel(GUI.localize("scale")));
        auto = new JCheckBox(GUI.localize("scale.auto"));
        auto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                scaleSlider.setEnabled(!auto.isSelected());
                imageSizeHasChanged();
            }
        });
        auto.setSelected(true);
        controlsPanel.add(auto);
        scaleSlider = new JSlider(30, 100, scale);
        scaleSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                if (!scaleSlider.getValueIsAdjusting()) {
                    scale = (byte) scaleSlider.getValue();
                    imageSizeHasChanged();
                }
            }
        });
        scaleSlider.setToolTipText(GUI.localize("scale.manual"));
        scaleSlider.setEnabled(false);
        controlsPanel.add(scaleSlider);

        return scrollPane;
    }

    public JPanel getControlsPanel() {
        return controlsPanel;
    }

    private void imageSizeHasChanged() {
        if (auto != null) {
            if (auto.isSelected()) {
                if (imagePanel != null) {
                    final Dimension d = scrollPane.getSize();
                    final int pseudoScale = (int) (Math.min(
                            (d.height * 0.99 / simulationHeight),
                            (d.width * 0.99 / simulationWidth)) * 100);
                    scale = pseudoScale >= 100 ? 100 : (byte) pseudoScale;
                }
            } else {
                scale = (byte) scaleSlider.getValue();
            }
        }
        final float zoom = (float) scale / 100;
        imagePanel
                .setPreferredSize(new Dimension((int) (simulationWidth * zoom),
                        (int) (simulationHeight * zoom)));
        SwingUtilities.updateComponentTreeUI(scrollPane);
        imageHasChanged();
    }

    private void imageHasChanged() {
        needRepaint = true;
        if (imagePanel == null || !imagePanel.isDisplayable()) {
            return;
        }
        imagePanel.repaint();
    }

    private void updateImage() {
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
            bimg = (BufferedImage) collisionsPanel.createImage(w, h);
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
                                    - offset_y, R * 2, R * 2, collisionsPanel);
                    final String label = atoms[i].toString();
                    final int width = g2.getFontMetrics().stringWidth(label);
                    g2.drawString(label, (int) atoms[i].getPhysicalPoint()
                            .getPositionX() - width / 2, (int) atoms[i]
                            .getPhysicalPoint().getPositionY() + text_offset_y);
                } else {
                    // draw a special spiky image and no label
                    g2.drawImage(Resource.getSpikyImage(), (int) atoms[i]
                            .getPhysicalPoint().getPositionX() - offset_x,
                            (int) atoms[i].getPhysicalPoint().getPositionY()
                                    - offset_y, R * 2, R * 2, collisionsPanel);
                }
            }
        }

        // draw the bonds
        g2.setPaint(new Color(0, 0, 0, 50));
        if (atoms != null) {
            for (final Atom atom : atoms) {
                final Iterator it = atom.getBonds().iterator();
                while (it.hasNext()) {
                    final Atom other = (Atom) it.next();
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
        if (getApplicationEngine().getLastUsedDraggingPoint() != null) {
            final DraggingPoint lastUsedDraggingPoint = getApplicationEngine()
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

    class ImagePanel extends JPanel implements Scrollable {

        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        public ImagePanel() {
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent event) {
                }

                @Override
                public void mouseEntered(final MouseEvent event) {
                }

                @Override
                public void mouseExited(final MouseEvent event) {
                }

                @Override
                public void mousePressed(final MouseEvent event) {
                    // who did we click on?
                    final Point p = event.getPoint();
                    final int R = (int) Atom.getAtomSize();
                    final float zoom = (float) scale / 100;
                    final Point2D.Float p1 = new Point2D.Float();
                    final Point2D.Float p2 = new Point2D.Float(p.x / zoom, p.y
                            / zoom);
                    for (int i = 0; i < latestAtomsCopy.length; i++) {
                        p1.x = latestAtomsCopy[i].getPhysicalPoint()
                                .getPositionX();
                        p1.y = latestAtomsCopy[i].getPhysicalPoint()
                                .getPositionY();
                        if (p2.distanceSq(p1) < R * R) {
                            getApplicationEngine().setDraggingPoint(
                                    new DraggingPoint((long) p2.x, (long) p2.y,
                                            i));
                            break;
                        }
                    }
                }

                @Override
                public void mouseReleased(final MouseEvent event) {
                    getApplicationEngine().setDraggingPoint(null);
                }
            });
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(final MouseEvent event) {
                    if (draggingPoint != null) {
                        final float zoom = (float) scale / 100;
                        getApplicationEngine().setDraggingPoint(
                                new DraggingPoint(
                                        (long) (event.getPoint().x / zoom),
                                        (long) (event.getPoint().y / zoom),
                                        draggingPoint.getWhichBeingDragging()));
                    }
                }

                @Override
                public void mouseMoved(final MouseEvent event) {
                    if (draggingPoint != null) {
                        final float zoom = (float) scale / 100;
                        getApplicationEngine().setDraggingPoint(
                                new DraggingPoint(
                                        (long) (event.getPoint().x / zoom),
                                        (long) (event.getPoint().y / zoom),
                                        draggingPoint.getWhichBeingDragging()));
                    }
                }
            });
        }

        @Override
        public void paint(final Graphics g) {
            super.paint(g);
            updateImage();
            final float zoom = (float) scale / 100;
            g.drawImage(bimg, 0, 0, (int) (bimg.getWidth() * zoom),
                    (int) (bimg.getHeight() * zoom), this);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableBlockIncrement(final Rectangle arg0,
                final int arg1, final int arg2) {
            return 1; // TODO
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        @Override
        public int getScrollableUnitIncrement(final Rectangle arg0,
                final int arg1, final int arg2) {
            return 1; // TODO
        }

    }

}
