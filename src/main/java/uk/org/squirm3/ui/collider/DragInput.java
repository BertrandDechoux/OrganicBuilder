package uk.org.squirm3.ui.collider;

import java.util.Collection;
import java.util.Iterator;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.ApplicationEngineEvent;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.DraggingPoint;
import uk.org.squirm3.model.IPhysicalPoint;

public class DragInput extends BorderPane {
	private final ApplicationEngine applicationEngine;
	
	private DraggingPoint draggingPoint;
	private Atom[] latestAtomsCopy;

	public DragInput(Node node, ApplicationEngine applicationEngine) {
		node.setOnMousePressed((e) -> setDraggingPoint(e));
		node.setOnMouseDragged((e) -> updateDraggingPoint(e));
		node.setOnMouseReleased((e) -> resetDraggingPoint());

		this.applicationEngine = applicationEngine;
		bindToApplicationEngine(applicationEngine);
	}

	private void bindToApplicationEngine(final ApplicationEngine applicationEngine) {
		applicationEngine.addListener(new AtomListener(applicationEngine), ApplicationEngineEvent.ATOMS);
	}

	private void resetDraggingPoint() {
		applicationEngine.setDraggingPoint(null);
		draggingPoint = null;
	}

	private void updateDraggingPoint(MouseEvent e) {
		if (draggingPoint != null) {
			draggingPoint = new DraggingPoint(e.getX(), e.getY(),
					draggingPoint.getWhichBeingDragging());
			applicationEngine.setDraggingPoint(draggingPoint);
		}
	}

	private void setDraggingPoint(MouseEvent event) {
		if (!isLeftClick(event)) {
			return;
		}
		double x = event.getX();
		double y = event.getY();
		double R = Atom.getAtomSize();

		final Point2D p2 = new Point2D(x, y);
		for (int i = 0; i < latestAtomsCopy.length; i++) {
			IPhysicalPoint atomPoint = latestAtomsCopy[i].getPhysicalPoint();
			final Point2D p1 = new Point2D(//
					atomPoint.getPositionX(), //
					atomPoint.getPositionY());
			if (p2.distance(p1) < Math.sqrt(R * R)) {
				draggingPoint = new DraggingPoint(p2.getX(), p2.getY(), i);
				applicationEngine.setDraggingPoint(draggingPoint);
				break;
			}
		}
	}

	private boolean isLeftClick(MouseEvent event) {
		return event.getButton() == MouseButton.PRIMARY;
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
		}
	}

}
