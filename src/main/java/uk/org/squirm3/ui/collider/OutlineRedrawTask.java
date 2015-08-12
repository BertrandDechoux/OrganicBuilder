package uk.org.squirm3.ui.collider;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class OutlineRedrawTask extends CanvasRedrawTask<Boolean> {

	public OutlineRedrawTask(Canvas canvas) {
		super(canvas);
	}

	@Override
	protected void redraw(GraphicsContext gc, double width, double height, Boolean data) {
		gc.clearRect(0, 0, width, height);
		gc.setFill(Color.BLUE);
		gc.setLineWidth(5);
		gc.strokeRoundRect( //
				0, //
				0, //
				width, //
				height, //
				9, //
				9);
	}

}
