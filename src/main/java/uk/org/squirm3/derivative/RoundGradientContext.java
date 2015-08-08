package uk.org.squirm3.derivative;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * This file is part of Organic Builder and is a derivative work from an
 * oreilly's example.
 * 
 * @author from oreilly, Bertrand Dechoux
 * 
 * see "http://www.oreilly.com/pub/a/oreilly/ask_tim/2001/codepolicy.html"
 * see "http://examples.oreilly.com/java2d/examples/RoundGradientContext.java"
 */

public class RoundGradientContext implements PaintContext {
    protected Point2D mPoint;
    protected Point2D mRadius;
    protected Color mC1, mC2;

    public RoundGradientContext(final Point2D p, final Color c1, final Point2D r, final Color c2) {
        mPoint = p;
        mC1 = c1;
        mRadius = r;
        mC2 = c2;
    }

    @Override
    public void dispose() {
    }

    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public Raster getRaster(final int x, final int y, final int w, final int h) {
        final WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);

        final int[] data = new int[w * h * 4];
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                final double distance = mPoint.distance(x + i, y + j);
                final double radius = mRadius.distance(0, 0);
                double ratio = distance / radius;
                if (ratio > 1.0) {
                    ratio = 1.0;
                }

                final int base = (j * w + i) * 4;
                data[base + 0] = (int) (mC1.getRed() + ratio * (mC2.getRed() - mC1.getRed()));
                data[base + 1] = (int) (mC1.getGreen() + ratio * (mC2.getGreen() - mC1.getGreen()));
                data[base + 2] = (int) (mC1.getBlue() + ratio * (mC2.getBlue() - mC1.getBlue()));
                data[base + 3] = (int) (mC1.getAlpha() + ratio * (mC2.getAlpha() - mC1.getAlpha()));
            }
        }
        raster.setPixels(0, 0, w, h, data);

        return raster;
    }
}
