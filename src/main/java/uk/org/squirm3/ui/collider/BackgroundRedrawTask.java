package uk.org.squirm3.ui.collider;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class BackgroundRedrawTask extends CanvasRedrawTask<Boolean> {
	
	public BackgroundRedrawTask(Canvas canvas) {
		super(canvas);
	}

	@Override
	protected void redraw(GraphicsContext gc, double width, double height, Boolean data) {
		gc.setFill(javafx.scene.paint.Color.WHITE);
		gc.fillRect(0, 0, width, height);
	}

}
