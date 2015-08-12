package uk.org.squirm3.model;

public final class FixedPoint implements IPhysicalPoint {
    public static final IPhysicalPoint ORIGIN = new FixedPoint(0, 0);

    private double x, y;

    public FixedPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FixedPoint(final IPhysicalPoint physicalPoint) {
        x = physicalPoint.getPositionX();
        y = physicalPoint.getPositionY();
    }

    @Override
    public double getPositionX() {
        return x;
    }

    @Override
    public double getPositionY() {
        return y;
    }

    @Override
    public double getSpeedX() {
        return 0;
    }

    @Override
    public double getSpeedY() {
        return 0;
    }

    @Override
    public double getAccelerationX() {
        return 0;
    }

    @Override
    public double getAccelerationY() {
        return 0;
    }

    @Override
    public boolean setPositionX(double x) {
        return false;
    }

    @Override
    public boolean setPositionY(double y) {
        return false;
    }

    @Override
    public boolean setSpeedX(double dx) {
        return false;
    }

    @Override
    public boolean setSpeedY(double dy) {
        return false;
    }

    @Override
    public boolean setAccelerationX(double ddx) {
        return false;
    }

    @Override
    public boolean setAccelerationY(double ddy) {
        return false;
    }

    @Override
    public IPhysicalPoint copy() {
        return this;
    }
}
