package uk.org.squirm3.data;

public final class FixedPoint implements IPhysicalPoint {

    private final float x, y;

    public FixedPoint(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public FixedPoint(final IPhysicalPoint physicalPoint) {
        x = physicalPoint.getPositionX();
        y = physicalPoint.getPositionY();
    }

    public float getPositionX() {
        return x;
    }

    public float getPositionY() {
        return y;
    }

    public float getSpeedX() {
        return 0;
    }

    public float getSpeedY() {
        return 0;
    }

    public float getAccelerationX() {
        return 0;
    }

    public float getAccelerationY() {
        return 0;
    }

    public boolean setPositionX(final float x) {
        return false;
    }

    public boolean setPositionY(final float y) {
        return false;
    }

    public boolean setSpeedX(final float dx) {
        return false;
    }

    public boolean setSpeedY(final float dy) {
        return false;
    }

    public boolean setAccelerationX(final float ddx) {
        return false;
    }

    public boolean setAccelerationY(final float ddy) {
        return false;
    }

    public IPhysicalPoint copy() {
        return this;
    }
}
