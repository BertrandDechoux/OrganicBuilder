package uk.org.squirm3.derivative;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/**
 * This file is part of Organic Builder and is a derivative work from an
 * oreilly's example.
 * 
 * @author from oreilly, Bertrand Dechoux
 * @see http://www.oreilly.com/pub/a/oreilly/ask_tim/2001/codepolicy.html
 * @see http://examples.oreilly.com/java2d/examples/RoundGradientPaint.java
 */

public class RoundGradientPaint implements Paint {

    protected Point2D mPoint;
    protected Point2D mRadius;
    protected Color mPointColor, mBackgroundColor;

    public RoundGradientPaint(final double x, final double y,
            final Color pointColor, final Point2D radius,
            final Color backgroundColor) {
        if (radius.distance(0, 0) <= 0) {
            throw new IllegalArgumentException("Radius must be greater than 0.");
        }
        mPoint = new Point2D.Double(x, y);
        mPointColor = pointColor;
        mRadius = radius;
        mBackgroundColor = backgroundColor;
    }

    public PaintContext createContext(final ColorModel cm,
            final Rectangle deviceBounds, final Rectangle2D userBounds,
            final AffineTransform xform, final RenderingHints hints) {
        final Point2D transformedPoint = xform.transform(mPoint, null);
        final Point2D transformedRadius = xform.deltaTransform(mRadius, null);
        return new RoundGradientContext(transformedPoint, mPointColor,
                transformedRadius, mBackgroundColor);
    }

    public int getTransparency() {
        final int a1 = mPointColor.getAlpha();
        final int a2 = mBackgroundColor.getAlpha();
        return (a1 & a2) == 0xff ? OPAQUE : TRANSLUCENT;
    }
}
