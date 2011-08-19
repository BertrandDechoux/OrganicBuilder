package uk.org.squirm3.ui.collider;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.DraggingPoint;

class ImagePanel extends JPanel implements Scrollable {
    private static final long serialVersionUID = 1L;

    private final AtomsPanel atomsPanel;

    public ImagePanel(AtomsPanel atomsPanel) {
        this.atomsPanel = atomsPanel;
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
                if (isRightClick(event)) {
                    System.out.println("right click");
                }
                if (isLeftClick(event)) {
                    setDraggingPoint(event);
                }

            }

            private void setDraggingPoint(final MouseEvent event) {
                final Point p = event.getPoint();
                final int R = (int) Atom.getAtomSize();
                final float zoom = (float) ImagePanel.this.atomsPanel.scale / 100;
                final Point2D.Float p1 = new Point2D.Float();
                final Point2D.Float p2 = new Point2D.Float(p.x / zoom, p.y
                        / zoom);
                for (int i = 0; i < ImagePanel.this.atomsPanel.latestAtomsCopy.length; i++) {
                    p1.x = ImagePanel.this.atomsPanel.latestAtomsCopy[i].getPhysicalPoint()
                            .getPositionX();
                    p1.y = ImagePanel.this.atomsPanel.latestAtomsCopy[i].getPhysicalPoint()
                            .getPositionY();
                    if (p2.distanceSq(p1) < R * R) {
                        ImagePanel.this.atomsPanel.applicationEngine
                                .setDraggingPoint(new DraggingPoint(
                                        (long) p2.x, (long) p2.y, i));
                        break;
                    }
                }
            }

            private boolean isLeftClick(final MouseEvent event) {
                return (event.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK;
            }

            private boolean isRightClick(final MouseEvent event) {
                return (event.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
            }

            @Override
            public void mouseReleased(final MouseEvent event) {
                ImagePanel.this.atomsPanel.applicationEngine.setDraggingPoint(null);
            }
        });
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(final MouseEvent event) {
                if (ImagePanel.this.atomsPanel.draggingPoint != null) {
                    final float zoom = (float) ImagePanel.this.atomsPanel.scale / 100;
                    ImagePanel.this.atomsPanel.applicationEngine.setDraggingPoint(new DraggingPoint(
                            (long) (event.getPoint().x / zoom),
                            (long) (event.getPoint().y / zoom),
                            ImagePanel.this.atomsPanel.draggingPoint.getWhichBeingDragging()));
                }
            }

            @Override
            public void mouseMoved(final MouseEvent event) {
                if (ImagePanel.this.atomsPanel.draggingPoint != null) {
                    final float zoom = (float) ImagePanel.this.atomsPanel.scale / 100;
                    ImagePanel.this.atomsPanel.applicationEngine.setDraggingPoint(new DraggingPoint(
                            (long) (event.getPoint().x / zoom),
                            (long) (event.getPoint().y / zoom),
                            ImagePanel.this.atomsPanel.draggingPoint.getWhichBeingDragging()));
                }
            }
        });
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        this.atomsPanel.updateImage();
        final float zoom = (float) this.atomsPanel.scale / 100;
        g.drawImage(this.atomsPanel.bimg, 0, 0, (int) (this.atomsPanel.bimg.getWidth() * zoom),
                (int) (this.atomsPanel.bimg.getHeight() * zoom), this);
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