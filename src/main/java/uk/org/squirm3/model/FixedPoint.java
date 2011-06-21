package uk.org.squirm3.model;

public final class FixedPoint implements IPhysicalPoint {
    public static final IPhysicalPoint ORIGIN = new FixedPoint(0, 0);

    private final float x, y;

    public FixedPoint(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public FixedPoint(final IPhysicalPoint physicalPoint) {
        x = physicalPoint.getPositionX();
        y = physicalPoint.getPositionY();
    }

    @Override
    public float getPositionX() {
        return x;
    }

    @Override
    public float getPositionY() {
        return y;
    }

    @Override
    public float getSpeedX() {
        return 0;
    }

    @Override
    public float getSpeedY() {
        return 0;
    }

    @Override
    public float getAccelerationX() {
        return 0;
    }

    @Override
    public float getAccelerationY() {
        return 0;
    }

    @Override
    public boolean setPositionX(final float x) {
        return false;
    }

    @Override
    public boolean setPositionY(final float y) {
        return false;
    }

    @Override
    public boolean setSpeedX(final float dx) {
        return false;
    }

    @Override
    public boolean setSpeedY(final float dy) {
        return false;
    }

    @Override
    public boolean setAccelerationX(final float ddx) {
        return false;
    }

    @Override
    public boolean setAccelerationY(final float ddy) {
        return false;
    }

    @Override
    public IPhysicalPoint copy() {
        return this;
    }
}
