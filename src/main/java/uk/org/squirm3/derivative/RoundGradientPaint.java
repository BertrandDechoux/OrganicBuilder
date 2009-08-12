package uk.org.squirm3.derivative;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;


/**
 * This file is part of Organic Builder and
 * is a derivative work from an oreilly's example.
 *
 * @author from oreilly, Bertrand Dechoux
 * @see http://www.oreilly.com/pub/a/oreilly/ask_tim/2001/codepolicy.html
 * @see http://examples.oreilly.com/java2d/examples/RoundGradientPaint.java
 */

public class RoundGradientPaint implements Paint {

    protected Point2D mPoint;
    protected Point2D mRadius;
    protected Color mPointColor, mBackgroundColor;

    public RoundGradientPaint(double x, double y, Color pointColor,
                              Point2D radius, Color backgroundColor) {
        if (radius.distance(0, 0) <= 0)
            throw new IllegalArgumentException("Radius must be greater than 0.");
        mPoint = new Point2D.Double(x, y);
        mPointColor = pointColor;
        mRadius = radius;
        mBackgroundColor = backgroundColor;
    }

    public PaintContext createContext(ColorModel cm,
                                      Rectangle deviceBounds, Rectangle2D userBounds,
                                      AffineTransform xform, RenderingHints hints) {
        Point2D transformedPoint = xform.transform(mPoint, null);
        Point2D transformedRadius = xform.deltaTransform(mRadius, null);
        return new RoundGradientContext(transformedPoint, mPointColor,
                transformedRadius, mBackgroundColor);
    }

    public int getTransparency() {
        int a1 = mPointColor.getAlpha();
        int a2 = mBackgroundColor.getAlpha();
        return (((a1 & a2) == 0xff) ? OPAQUE : TRANSLUCENT);
    }
}
