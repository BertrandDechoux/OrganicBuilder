package uk.org.squirm3.ui.collider;

import java.awt.Dimension;
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
import javafx.scene.image.Image;
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
import uk.org.squirm3.model.IPhysicalPoint;
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
    
    private Canvas backgroundCanvas;
    private Canvas atomsCanvas;
    private Canvas draggingPointCanvas;
    private Canvas latestDraggingPointCanvas;
    private Canvas outlineCanvas;
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
		setCenter(root);

        this.applicationEngine = applicationEngine;
        this.spikyImage = spikyImage;
        needRepaint = true;
        scale = 100;
        imageSizeHasChanged();

        bindToApplicationEngine(applicationEngine);

    }

	private void setupCanvasses(double width, double height) {
		backgroundCanvas = createCanvas(width, height);
		atomsCanvas = createCanvas(width, height);
		draggingPointCanvas = createCanvas(width, height);
		latestDraggingPointCanvas = createCanvas(width, height);
		outlineCanvas = createCanvas(width, height);
		
		root.getChildren().clear();
		root.getChildren().addAll(//
				backgroundCanvas, atomsCanvas, draggingPointCanvas, latestDraggingPointCanvas, outlineCanvas);
	}

	private Canvas createCanvas(double width, double height) {
		Canvas canvas = new Canvas(width, height);
		canvas.setTranslateX(100);
		canvas.setTranslateY(100);
		return canvas;
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
		double R = Atom.getAtomSize() - 2;
		double w = 2 * R;
		double h = 2 * R;
		Color baseColor = atomColors.get(type);
		final int colorOffset = 220;
		int red = addOffset(baseColor.getRed(), colorOffset);
		int green = addOffset(baseColor.getGreen(), colorOffset);
		int blue = addOffset(baseColor.getBlue(), colorOffset);
		final Color lightColor = Color.rgb(red, green, blue);
		g2.setFill(new RadialGradient(//
				0, //
				0, //
				x + w / 2, //
				y + h / 2, //
				R, //
				false, //
				CycleMethod.NO_CYCLE, //
				new Stop(0, lightColor), //
				new Stop(1, baseColor)));
		g2.fillOval(x, y, w, h);
    }

	private static int addOffset(double component, final int colorOffset) {
		int max = 255;
		int scaledComponent = (int) component * max;
		return Math.min(scaledComponent + colorOffset, max);
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

    public void updateImage() {
		Platform.runLater(() -> {
			if (!needRepaint) {
				return;
			}
			needRepaint = false;
			double R = Atom.getAtomSize();
			final Atom[] atoms = latestAtomsCopy;

			// get the dimensions
			double w = simulationWidth;
			double h = simulationHeight;

			{
				Canvas localBackgroundCanvas = backgroundCanvas;
				if (localBackgroundCanvas == null || localBackgroundCanvas.getWidth() != w
						|| localBackgroundCanvas.getHeight() != h) {
					setupCanvasses(w, h);
				}
			}

			Canvas localBackgroundCanvas = backgroundCanvas;
			Canvas localAtomsCanvas = atomsCanvas;
			Canvas localDraggingPointCanvas = latestDraggingPointCanvas;
			Canvas localLatestDraggingPointCanvas = latestDraggingPointCanvas;
			Canvas localOutlineCanvas = outlineCanvas;

			drawBackground(w, h, localBackgroundCanvas.getGraphicsContext2D());
			
			localAtomsCanvas.getGraphicsContext2D().clearRect(0, 0, w, h);
			drawAtoms(R, atoms, localAtomsCanvas.getGraphicsContext2D());
			drawBonds(R, atoms, localAtomsCanvas.getGraphicsContext2D());
			
			localDraggingPointCanvas.getGraphicsContext2D().clearRect(0, 0, w, h);
			drawDraggingPoint(atoms, draggingPoint, localDraggingPointCanvas.getGraphicsContext2D(), //
					5, Color.rgb(0, 0, 0, 0.3));
			
			localLatestDraggingPointCanvas.getGraphicsContext2D().clearRect(0, 0, w, h);
			drawDraggingPoint(atoms, applicationEngine.getLastUsedDraggingPoint(),
					localLatestDraggingPointCanvas.getGraphicsContext2D(), //
					1, Color.rgb(200, 0, 0, 0.3));
			
			drawColliderOutline(localOutlineCanvas.getGraphicsContext2D());
		});
    }

	private void drawColliderOutline(GraphicsContext gc) {
		gc.setFill(Color.BLUE);
        gc.strokeRoundRect( //
        		0, //
        		0, //
        		simulationWidth, //
        		simulationHeight, //
        		9, //
        		9);
	}

	private void drawDraggingPoint(Atom[] atoms, DraggingPoint draggingPoint, GraphicsContext gc, double lineWidth, Color color) {
		if (draggingPoint != null) {
        	gc.setLineWidth(5);
        	gc.setFill(Color.rgb(0, 0, 0, 0.3));
            Atom draggedAtomPoint = atoms[draggingPoint.getWhichBeingDragging()];
			gc.strokeLine(//
            		draggingPoint.getX(),//
            		draggingPoint.getY(),//
                    draggedAtomPoint.getPhysicalPoint().getPositionX(),//
                    draggedAtomPoint.getPhysicalPoint().getPositionY());
        }
	}

	private void drawBonds(double r, Atom[] atoms, GraphicsContext gc) {
		gc.setFill(Color.rgb(0, 0, 0, 0.1));
        if (atoms != null) {
            for (final Atom atom : atoms) {
            	for (Atom other : atom.getBonds()) {
                    IPhysicalPoint atomPoint = atom.getPhysicalPoint();
                    IPhysicalPoint otherPoint = other.getPhysicalPoint();
					double x1 = atomPoint.getPositionX();
					double y1 = atomPoint.getPositionY();
					double dx = otherPoint.getPositionX() - x1;
					double dy = otherPoint.getPositionY() - y1;
					double d = Math.sqrt(dx * dx + dy * dy);
					double x_cut = dx * r * 0.8 / d;
					double y_cut = dy * r * 0.8 / d;
                    gc.strokeLine(//
                    		x1 + x_cut,//
                    		y1 + y_cut,//
                            x1 + dx - x_cut,//
                            y1 + dy - y_cut);
                }
            }
        }
	}

	private void drawAtoms(double r, Atom[] atoms, GraphicsContext gc) {
		gc.setLineWidth(4);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, r));
        gc.setFill(Color.BLACK);
        if (atoms != null) {
            double offset_x = r;
            double offset_y = r;
            double text_offset_x = 2 * (r * 8.0 / 22.0);
            double text_offset_y = (r * 8.0 / 22.0);
            for (Atom atom : atoms) {
                IPhysicalPoint atomPoint = atom.getPhysicalPoint();
				if (!atom.isKiller()) {
                    // draw the normal colour atom image and label it
                	drawAtomsImage((BasicType)atom.getType(), gc, //
                			atomPoint.getPositionX() - offset_x, //
                			atomPoint.getPositionY() - offset_y);
                    final String label = atom.toString();
                    gc.setFill(Color.BLACK);
					gc.fillText(label, //
							atomPoint.getPositionX() - text_offset_x, //
							atomPoint.getPositionY() + text_offset_y);
                } else {
                    // draw a special spiky image and no label
                	gc.drawImage(spikyImage, //
                			atomPoint.getPositionX() - offset_x, //
                			atomPoint.getPositionY() - offset_y, //
                            r * 2,//
                            r * 2);
                }
            }
        }
	}

	private void drawBackground(double w, double h, GraphicsContext gc) {
		gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillRect(0, 0, w, h);
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
