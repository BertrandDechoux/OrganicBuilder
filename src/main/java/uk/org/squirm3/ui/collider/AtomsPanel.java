package uk.org.squirm3.ui.collider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.DraggingPoint;
import uk.org.squirm3.ui.Utils;

public class AtomsPanel extends BorderPane {
    private double simulationWidth;
    private double simulationHeight;
    private double scale;

    private final ApplicationEngine applicationEngine;
    
    private final BackgroundRedrawTask backgroundRedrawTask;
    private final AtomsRedrawTask atomsRedrawTask;
    private final DraggingPointRedrawTask draggingPointRedrawTask;
    private final OutlineRedrawTask outlineRedrawTask;
    
    public AtomsPanel(final ApplicationEngine applicationEngine,
            final Image spikyImage) {
    	setMinSize(300, 300);
    	Utils.background(this, Color.GREY);
    	Utils.defaultBorder(this);
    	setPadding(new Insets(12));

		Group root = new Group();
		setCenter(root);
		
		Canvas backgroundCanvas = new Canvas();
		backgroundRedrawTask = new BackgroundRedrawTask(backgroundCanvas);
		Canvas atomsCanvas = new Canvas();
		atomsRedrawTask = new AtomsRedrawTask(atomsCanvas, spikyImage);
		Canvas draggingPointCanvas = new Canvas();
		draggingPointRedrawTask = new DraggingPointRedrawTask(draggingPointCanvas, 5, Color.rgb(0, 0, 0, 0.3));
		Canvas outlineCanvas = new Canvas();
		outlineRedrawTask = new OutlineRedrawTask(outlineCanvas);

		new DragInput(outlineCanvas, applicationEngine);
		
		root.getChildren().addAll(//
				backgroundCanvas, atomsCanvas, draggingPointCanvas, outlineCanvas);
		
        this.applicationEngine = applicationEngine;
        scale = -1;
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

    private void imageSizeHasChanged() {
    	Platform.runLater(() -> {
	        final Configuration configuration = applicationEngine
	                .getConfiguration();
	        if (configuration != null) {
	        	double previousHeight = simulationHeight;
	        	double previousWidth = simulationWidth;
	            simulationHeight =  configuration.getHeight();
	            simulationWidth = configuration.getWidth();
	            double height = getHeight();
	            double width = getWidth();
	            double hScale = height / simulationHeight;
	            double wScale = width / simulationWidth;
	            double scale = Math.min(hScale, wScale);
	            if (previousHeight != simulationHeight || previousWidth != simulationWidth || this.scale != scale) {
	            	this.scale = scale;
	            	backgroundRedrawTask.setSize(simulationHeight, simulationWidth, scale);
	            	atomsRedrawTask.setSize(simulationHeight, simulationWidth, scale);
	            	draggingPointRedrawTask.setSize(simulationHeight, simulationWidth, scale);
	            	outlineRedrawTask.setSize(simulationHeight, simulationWidth, scale);
	            	
	            	backgroundRedrawTask.requestRedraw(true);
	            	outlineRedrawTask.requestRedraw(true);
	            }
	        }
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
            DraggingPoint draggingPoint = applicationEngine.getCurrentDraggingPoint();
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
            Atom[] latestAtomsCopy = new Atom[c.size()];
            int i = 0;
            while (it.hasNext()) {
                latestAtomsCopy[i] = it.next();
                i++;
            }
            List<Atom> atoms = Arrays.asList(latestAtomsCopy);
			atomsRedrawTask.requestRedraw(atoms);
            draggingPointRedrawTask.setAtoms(atoms);
            DraggingPoint draggingPoint = applicationEngine.getCurrentDraggingPoint();
            draggingPointRedrawTask.requestRedraw(Optional.ofNullable(draggingPoint));
        }
    }

}
