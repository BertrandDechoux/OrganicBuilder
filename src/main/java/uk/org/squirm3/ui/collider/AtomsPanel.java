package uk.org.squirm3.ui.collider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.DraggingPoint;
import uk.org.squirm3.model.IPhysicalPoint;
import uk.org.squirm3.ui.Utils;

public class AtomsPanel extends BorderPane {
    DraggingPoint draggingPoint;
    Atom[] latestAtomsCopy;

    private double simulationWidth;
    private double simulationHeight;

    //private BufferedImage bimg;
    byte scale;

    final ApplicationEngine applicationEngine;
    
    private final BackgroundRedrawTask backgroundRedrawTask;
    private final AtomsRedrawTask atomsRedrawTask;
    private final DraggingPointRedrawTask draggingPointRedrawTask;
    private final OutlineRedrawTask outlineRedrawTask;
    
    private Group root;

    public AtomsPanel(final ApplicationEngine applicationEngine,
            final Image spikyImage) {
    	setMinSize(300, 300);
    	Utils.background(this, Color.GREY);
    	Utils.defaultBorder(this);
    	setPadding(new Insets(12));

		root = new Group();
		setCenter(root);
		
		backgroundRedrawTask = new BackgroundRedrawTask();
		atomsRedrawTask = new AtomsRedrawTask(spikyImage);
		draggingPointRedrawTask = new DraggingPointRedrawTask(5, Color.rgb(0, 0, 0, 0.3));
		outlineRedrawTask = new OutlineRedrawTask();

        this.applicationEngine = applicationEngine;
        scale = 100;
        imageSizeHasChanged();

        bindToApplicationEngine(applicationEngine);

    }

	private void setupCanvasses(double width, double height) {
		Canvas backgroundCanvas = backgroundRedrawTask.updateCanvas(createCanvas(width, height));
		backgroundRedrawTask.requestRedraw(backgroundCanvas);

		Canvas atomsCanvas = atomsRedrawTask.updateCanvas(createCanvas(width, height));
		
		Canvas draggingPointCanvas = draggingPointRedrawTask.updateCanvas(createCanvas(width, height));
		
		Canvas outlineCanvas = outlineRedrawTask.updateCanvas(createCanvas(width, height));
		outlineRedrawTask.requestRedraw(outlineCanvas);
		outlineCanvas.setOnMousePressed((e) -> setDraggingPoint(e));
		outlineCanvas.setOnMouseDragged((e) -> updateDraggingPoint(e));
		outlineCanvas.setOnMouseMoved((e) -> updateDraggingPoint(e));
		outlineCanvas.setOnMouseReleased((e) -> resetDraggingPoint());
		
		root.getChildren().clear();
		root.getChildren().addAll(//
				backgroundCanvas, atomsCanvas, draggingPointCanvas, outlineCanvas);
	}

	private void resetDraggingPoint() {
		applicationEngine.setDraggingPoint(null);
	}

	private void updateDraggingPoint(MouseEvent e) {
		if (draggingPoint != null) {
			double zoom = (double) scale / 100;
			DraggingPoint newDraggingPoint = new DraggingPoint((long) (e.getX() / zoom), (long) (e.getY() / zoom),
					draggingPoint.getWhichBeingDragging());
			applicationEngine.setDraggingPoint(newDraggingPoint);
		}
	}

	private Canvas createCanvas(double width, double height) {
		Canvas canvas = new Canvas(width, height);
		canvas.setTranslateX(100);
		canvas.setTranslateY(100);
		return canvas;
	}
	
    private void setDraggingPoint(MouseEvent event) {
    	if (!isLeftClick(event)) {
    		return;
    	}
    	double x = event.getX();
    	double y = event.getY();
        double R = Atom.getAtomSize();
        double zoom = (double) scale / 100;
        
        final Point2D p2 = new Point2D(x / zoom, y / zoom);
        for (int i = 0; i < latestAtomsCopy.length; i++) {
            IPhysicalPoint atomPoint = latestAtomsCopy[i].getPhysicalPoint();
            final Point2D p1 = new Point2D(//
            		atomPoint.getPositionX(),//
            		atomPoint.getPositionY());
            if (p2.distance(p1) < Math.sqrt(R * R)) {
                DraggingPoint newDraggingPoint = new DraggingPoint((long) p2.getX(),
				        (long) p2.getY(), i);
				applicationEngine.setDraggingPoint(newDraggingPoint);
                break;
            }
        }
    }
    private boolean isLeftClick(MouseEvent event) {
        return event.getButton()  == MouseButton.PRIMARY;
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

    private void imageSizeHasChanged() {
    	Platform.runLater(() -> {
	        final Configuration configuration = applicationEngine
	                .getConfiguration();
	        if (configuration != null) {
	        	double previousHeight = simulationHeight;
	        	double previousWidth = simulationWidth;
	            simulationHeight =  configuration.getHeight();
	            simulationWidth = configuration.getWidth();
	            if (previousHeight != simulationHeight && previousWidth != simulationWidth) {
					setupCanvasses(simulationWidth, simulationHeight);
	            }
	        }
	        /* XXX handle zoom
	         
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
	        */

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
            draggingPointRedrawTask.requestRedraw(Optional.ofNullable(draggingPoint));
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
            List<Atom> atoms = Arrays.asList(latestAtomsCopy);
			atomsRedrawTask.requestRedraw(atoms);
            draggingPointRedrawTask.setAtoms(atoms);
            draggingPoint = applicationEngine.getCurrentDraggingPoint();
            draggingPointRedrawTask.requestRedraw(Optional.ofNullable(draggingPoint));
        }
    }

}
