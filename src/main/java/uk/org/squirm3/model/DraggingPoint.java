package uk.org.squirm3.model;

public class DraggingPoint {
    private final double x;
    private final double y;
    private final int whichBeingDragging;

    public DraggingPoint(double x, double y, int whichBeingDragging) {
        this.x = x;
        this.y = y;
        this.whichBeingDragging = whichBeingDragging;
    }

    public int getWhichBeingDragging() {
        return whichBeingDragging;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DraggingPoint other = (DraggingPoint) obj;
        if (whichBeingDragging != other.whichBeingDragging) {
            return false;
        }
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        return true;
    }

}
