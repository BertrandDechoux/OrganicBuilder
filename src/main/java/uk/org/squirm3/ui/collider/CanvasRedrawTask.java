package uk.org.squirm3.ui.collider;

import java.util.concurrent.atomic.AtomicReference;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public abstract class CanvasRedrawTask<T> extends AnimationTimer {
	private final AtomicReference<T> data = new AtomicReference<T>(null);
	private Canvas canvas;

	public Canvas updateCanvas(Canvas canvas) {
		data.set(null);
		this.canvas = canvas;
		return canvas;
	}

	public void requestRedraw(T dataToDraw) {
		data.set(dataToDraw);
		start();
	}

	public void handle(long now) {
		Canvas localCanvas = canvas;
		if (localCanvas == null) {
			return;
		}
		T dataToDraw = data.getAndSet(null);
		if (dataToDraw != null) {
			redraw(//
					localCanvas.getGraphicsContext2D(), //
					localCanvas.getWidth(), //
					localCanvas.getHeight(), //
					dataToDraw);
		}
	}

	protected abstract void redraw(GraphicsContext gc, double width, double height, T data);
}
