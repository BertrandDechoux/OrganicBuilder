package uk.org.squirm3.data;

/**  
${my.copyright}
 */

public class DraggingPoint {
    private final long x;
    private final long y;
    private final int whichBeingDragging;

    public DraggingPoint(long x, long y, int whichBeingDragging) {
        this.x = x;
        this.y = y;
        this.whichBeingDragging = whichBeingDragging;
    }

    public int getWhichBeingDragging() {
        return whichBeingDragging;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DraggingPoint other = (DraggingPoint) obj;
        if (whichBeingDragging != other.whichBeingDragging)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

}
