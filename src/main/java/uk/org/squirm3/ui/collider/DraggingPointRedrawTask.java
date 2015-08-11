package uk.org.squirm3.ui.collider;

import java.util.List;
import java.util.Optional;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.DraggingPoint;

public class DraggingPointRedrawTask extends CanvasRedrawTask<Optional<DraggingPoint>> {
	private final double lineWidth;
	private final Color lineColor;
	private List<Atom> atoms;

	public DraggingPointRedrawTask(double lineWidth, Color lineColor) {
		this.lineWidth = lineWidth;
		this.lineColor = lineColor;
	}

	// XXX not the best way...
	public void setAtoms(List<Atom> atoms) {
		this.atoms = atoms;
	}

	@Override
	protected void redraw(GraphicsContext gc, double width, double height,
			Optional<DraggingPoint> optionalDraggingPoint) {
		gc.clearRect(0, 0, width, height);
		gc.setLineWidth(lineWidth);
		gc.setFill(lineColor);
		if (optionalDraggingPoint.isPresent()) {
			DraggingPoint draggingPoint = optionalDraggingPoint.get();
			Atom draggedAtomPoint = atoms.get(draggingPoint.getWhichBeingDragging());
			gc.strokeLine(//
					draggingPoint.getX(), //
					draggingPoint.getY(), //
					draggedAtomPoint.getPhysicalPoint().getPositionX(), //
					draggedAtomPoint.getPhysicalPoint().getPositionY());
		}
	}

}
