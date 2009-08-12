package uk.org.squirm3.data;

/**
 * ${my.copyright}
 */

public final class FixedPoint implements IPhysicalPoint {

    private final float x, y;

    public FixedPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public FixedPoint(IPhysicalPoint physicalPoint) {
        this.x = physicalPoint.getPositionX();
        this.y = physicalPoint.getPositionY();
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

    public boolean setPositionX(float x) {
        return false;
    }

    public boolean setPositionY(float y) {
        return false;
    }

    public boolean setSpeedX(float dx) {
        return false;
    }

    public boolean setSpeedY(float dy) {
        return false;
    }

    public boolean setAccelerationX(float ddx) {
        return false;
    }

    public boolean setAccelerationY(float ddy) {
        return false;
    }

    public IPhysicalPoint copy() {
        return this;
    }
}
