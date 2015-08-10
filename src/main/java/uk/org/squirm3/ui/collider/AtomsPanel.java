package uk.org.squirm3.ui.collider;

import java.awt.Dimension;
import java.awt.Image;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.DraggingPoint;
import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.ui.Utils;

public class AtomsPanel extends BorderPane {
    DraggingPoint draggingPoint;
    Atom[] latestAtomsCopy;

    private int simulationWidth;
    private int simulationHeight;

    //private BufferedImage bimg;
    private boolean needRepaint = true;
    byte scale;

    private final JPanel imagePanel;

    // yellow, grey, blue, purple, red, green
    private static final Map<BasicType, Color> atomColors;
    static {
        atomColors = new HashMap<BasicType, Color>();
        atomColors.put(BasicType.A, Color.web("#bdcf00"));
        atomColors.put(BasicType.B, Color.web("#5f5f5f"));
        atomColors.put(BasicType.C, Color.web("#0773db"));
        atomColors.put(BasicType.D, Color.web("#ee10ac"));
        atomColors.put(BasicType.E, Color.web("#ef160f"));
        atomColors.put(BasicType.F, Color.web("#00df06"));
    }
    private final Image spikyImage;

    final ApplicationEngine applicationEngine;
    
    private GraphicsContext gc;
    private Canvas canvas;
    private Group root;

    public AtomsPanel(final ApplicationEngine applicationEngine,
            final Image spikyImage) {
    	setMinSize(300, 300);
    	Utils.background(this, Color.GREY);
    	Utils.defaultBorder(this);
    	setPadding(new Insets(12));

        imagePanel = new ImagePanel(this);
        imagePanel.setMinimumSize(new Dimension(300, 300));
		final SwingNode swingNode = new SwingNode();
		swingNode.setContent(imagePanel);
		
		root = new Group();
		canvas = new Canvas(200, 200);
		canvas.setTranslateX(100);
		canvas.setTranslateY(100);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		setCenter(root);

        this.applicationEngine = applicationEngine;
        this.spikyImage = spikyImage;
        needRepaint = true;
        scale = 100;
        imageSizeHasChanged();

        bindToApplicationEngine(applicationEngine);

    }
    
    @Override
    public void resize(double width, double height) {
    	super.resize(width, height);
    	imageSizeHasChanged();
    }

    private void bindToApplicationEngine(
            final ApplicationEngine applicationEngine) {
        applicationEngine.addListener(new AtomListener(applicationEngine),
                ApplicationEngineEvent.ATOMS);
        applicationEngine.addListener(new SizeListener(),
                ApplicationEngineEvent.CONFIGURATION);
        applicationEngine.addListener(new DraggingPointListener(
                applicationEngine), ApplicationEngineEvent.DRAGGING_POINT);
    }
    
    private static void drawAtomsImage(BasicType type, GraphicsContext g2, double x, double y) {
        // size
        final float R = Atom.getAtomSize() - 2;
        final int w = (int) (2 * R);
        final int h = (int) (2 * R);
        Color baseColor = atomColors.get(type);
        final int colorOffset = 220;
        final double red = baseColor.getRed() * 255 < 255 - colorOffset ? baseColor
                .getRed() * 255 + colorOffset : 255;
        final double green = baseColor.getGreen() * 255 < 255 - colorOffset
                ? baseColor.getGreen() * 255 + colorOffset
                : 255;
        final double blue = baseColor.getBlue() * 255 < 255 - colorOffset
                ? baseColor.getBlue() * 255 + colorOffset
                : 255;
        final Color lightColor = Color.rgb((int)red, (int)green, (int)blue);
        g2.setFill(new RadialGradient(
        		0,//
        		0,//
        		x + w/2,//
        		y + h/2,//
        		R,//
        		false,//
        		CycleMethod.NO_CYCLE,//
        		new Stop(0, lightColor),//
        		new Stop(1, baseColor)
        		));
        g2.fillOval(x, y, w, h);
    }

    private void imageSizeHasChanged() {
    	Platform.runLater(() -> {
	        final Configuration configuration = applicationEngine
	                .getConfiguration();
	        if (configuration != null) {
	            simulationHeight = (int) configuration.getHeight();
	            simulationWidth = (int) configuration.getWidth();
	        }
	        if (imagePanel != null) {
	        	double height = getHeight();
	        	double width = getWidth();
	            final int pseudoScale = (int) (Math.min(
	                    (height * 0.99 / simulationHeight),
	                    (width * 0.99 / simulationWidth)) * 100);
	            scale = pseudoScale >= 100 ? 100 : (byte) pseudoScale;
	        }
	        final float zoom = (float) scale / 100;
	        imagePanel
	                .setPreferredSize(new Dimension((int) (simulationWidth * zoom),
	                        (int) (simulationHeight * zoom)));
	        imageHasChanged();
    	});
    }

    private void imageHasChanged() {
        needRepaint = true;
        if (imagePanel == null || !imagePanel.isDisplayable()) {
            return;
        }
        imagePanel.repaint();
        updateImage();
    }
    
    public void updateCanvas() {
		Platform.runLater(() -> {
			final int w = simulationWidth;
			final int h = simulationHeight;
			if (canvas == null || canvas.getWidth() != w || canvas.getHeight() != h) {
				root.getChildren().remove(canvas);
				canvas = new Canvas(w, h);
				canvas.setTranslateX(100);
				canvas.setTranslateY(100);
				gc = canvas.getGraphicsContext2D();
				root.getChildren().add(canvas);
			}
		});
    }

    public void updateImage() {
    	Platform.runLater(() -> {
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

        updateCanvas();
        if (canvas == null) {
            return;// collisionsPanel is not displayable
        }

        // create graphics
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.clearRect(0, 0, w, h);
        gc.fillRect(0, 0, w, h);

        // draw the atoms themselves
        gc.setLineWidth(4);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, R));
        gc.setFill(Color.BLACK);
        if (atoms != null) {
            final double offset_x = R;
            final double offset_y = R;
            final double text_offset_x = 2 * (R * 8.0 / 22.0);
            final double text_offset_y = (R * 8.0 / 22.0);
            for (int i = 0; i < atoms.length; i++) {
                if (!atoms[i].isKiller()) {
                    // draw the normal colour atom image and label it
                	drawAtomsImage((BasicType)atoms[i].getType(), gc, //
                			atoms[i].getPhysicalPoint().getPositionX() - offset_x, //
                			atoms[i].getPhysicalPoint().getPositionY() - offset_y);
                    final String label = atoms[i].toString();
                    gc.setFill(Color.BLACK);
					gc.fillText(label, //
							atoms[i].getPhysicalPoint().getPositionX() - text_offset_x, //
							atoms[i].getPhysicalPoint().getPositionY() + text_offset_y);
                } else {
                    // draw a special spiky image and no label
                	// XXX later
                	gc.drawImage(null /*spikyImage*/, //
                    		atoms[i].getPhysicalPoint().getPositionX() - offset_x, //
                            atoms[i].getPhysicalPoint().getPositionY() - offset_y, //
                            R * 2,//
                            R * 2);
                }
            }
        }

        // draw the bonds
        gc.setFill(Color.rgb(0, 0, 0, 0.1));
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
                    gc.strokeLine(//
                    		x1 + x_cut,//
                    		y1 + y_cut,//
                            x1 + dx - x_cut,//
                            y1 + dy - y_cut);
                }
            }
        }

        // draw the dragging line if currently dragging
        if (draggingPoint != null) {
        	gc.setLineWidth(5);
        	gc.setFill(Color.rgb(0, 0, 0, 0.3));
            gc.strokeLine(//
            		draggingPoint.getX(),//
            		draggingPoint.getY(),//
                    atoms[draggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionX(),//
                    atoms[draggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionY());
            gc.setLineWidth(4); // else the stroke would have been
                                              // changed
            // when outlining the collider area
        }

        // draw the dragging point used
        if (applicationEngine.getLastUsedDraggingPoint() != null) {
            final DraggingPoint lastUsedDraggingPoint = applicationEngine
                    .getLastUsedDraggingPoint();
        	gc.setLineWidth(1);
        	gc.setFill(Color.rgb(200, 0, 0, 0.3));
            gc.strokeLine(//
            		lastUsedDraggingPoint.getX(),//
                    lastUsedDraggingPoint.getY(),//
                    atoms[lastUsedDraggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionX(),//
                    atoms[lastUsedDraggingPoint.getWhichBeingDragging()].getPhysicalPoint().getPositionY());
            gc.setLineWidth(4); // else the stroke would have been
                                              // changed
            // when outlining the collider area
        }

        // outline the collider area
        gc.setFill(Color.BLUE);
        gc.strokeRoundRect( //
        		0, //
        		0, //
        		simulationWidth, //
        		simulationHeight, //
        		9, //
        		9);
    	});
    }

    private final class SizeListener implements Listener {
        @Override
        public void propertyHasChanged() {
            imageSizeHasChanged();
        }
    }

    private final class DraggingPointListener implements Listener {
        private final ApplicationEngine applicationEngine;
        private DraggingPointListener(final ApplicationEngine applicationEngine) {
            this.applicationEngine = applicationEngine;
        }
        @Override
        public void propertyHasChanged() {
            draggingPoint = applicationEngine.getCurrentDraggingPoint();
            imageHasChanged();
        }
    }

    private final class AtomListener implements Listener {
        private final ApplicationEngine applicationEngine;
        private AtomListener(final ApplicationEngine applicationEngine) {
            this.applicationEngine = applicationEngine;
        }
        @Override
        public void propertyHasChanged() {
            final Collection<? extends Atom> c = applicationEngine.getAtoms();
            final Iterator<? extends Atom> it = c.iterator();
            latestAtomsCopy = new Atom[c.size()];
            int i = 0;
            while (it.hasNext()) {
                latestAtomsCopy[i] = it.next();
                i++;
            }
            imageHasChanged();
        }
    }

}
