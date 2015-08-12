package uk.org.squirm3.ui.collider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.IPhysicalPoint;
import uk.org.squirm3.model.type.def.BasicType;

public class AtomsRedrawTask extends CanvasRedrawTask<List<Atom>> {
	private static final Map<BasicType, Color> atomColors;

	static {
		atomColors = new HashMap<BasicType, Color>();
		atomColors.put(BasicType.A, Color.web("#bdcf00")); // yellow
		atomColors.put(BasicType.B, Color.web("#5f5f5f")); // grey
		atomColors.put(BasicType.C, Color.web("#0773db")); // blue
		atomColors.put(BasicType.D, Color.web("#ee10ac")); // purple
		atomColors.put(BasicType.E, Color.web("#ef160f")); // red
		atomColors.put(BasicType.F, Color.web("#00df06")); // green
	}

	private final Image spikyImage;

	public AtomsRedrawTask(Canvas canvas, Image spikyImage) {
		super(canvas);
		this.spikyImage = spikyImage;
	}

	@Override
	protected void redraw(GraphicsContext gc, double width, double height, List<Atom> atoms) {
		double R = Atom.getAtomSize();
		gc.clearRect(0, 0, width, height);
		drawAtoms(gc, R, atoms);
		drawBonds(gc, R, atoms);
	}

	private void drawAtoms(GraphicsContext gc, double r, List<Atom> atoms) {
		gc.setLineWidth(4);
		gc.setFont(Font.font("Arial", FontWeight.BOLD, r));
		gc.setFill(Color.BLACK);
		double offset_x = r;
		double offset_y = r;
		double text_offset_x = 2 * (r * 8.0 / 22.0);
		double text_offset_y = (r * 8.0 / 22.0);
		for (Atom atom : atoms) {
			IPhysicalPoint atomPoint = atom.getPhysicalPoint();
			if (!atom.isKiller()) {
				// draw the normal colour atom image and label it
				drawAtomsImage((BasicType) atom.getType(), gc, //
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
						r * 2, //
						r * 2);
			}
		}
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

	private void drawBonds(GraphicsContext gc, double r, List<Atom> atoms) {
		gc.setFill(Color.rgb(0, 0, 0, 0.1));
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
						x1 + x_cut, //
						y1 + y_cut, //
						x1 + dx - x_cut, //
						y1 + dy - y_cut);
			}
		}
	}
}
