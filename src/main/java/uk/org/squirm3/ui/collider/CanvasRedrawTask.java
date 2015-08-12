package uk.org.squirm3.ui.collider;

import java.util.concurrent.atomic.AtomicReference;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public abstract class CanvasRedrawTask<T> extends AnimationTimer {
	private final AtomicReference<T> data = new AtomicReference<T>(null);
	private Canvas canvas;
	
	public CanvasRedrawTask(Canvas canvas) {
		this.canvas = canvas;
	}
	
	public void setSize(double width, double height, double scale) {
		canvas.setWidth(width);
		canvas.setHeight(height);
		canvas.setScaleX(scale);
		canvas.setScaleY(scale);
	}

	public void requestRedraw(T dataToDraw) {
		data.set(dataToDraw);
		start();
	}

	public void handle(long now) {
		T dataToDraw = data.getAndSet(null);
		if (dataToDraw != null) {
			redraw(//
					canvas.getGraphicsContext2D(), //
					canvas.getWidth(), //
					canvas.getHeight(), //
					dataToDraw);
		}
	}

	protected abstract void redraw(GraphicsContext gc, double width, double height, T data);
}
