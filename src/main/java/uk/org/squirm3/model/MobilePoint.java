package uk.org.squirm3.model;

public class MobilePoint implements IPhysicalPoint {

    private double x, y, dx, dy, ddx, ddy;

    public MobilePoint() {
        this(0, 0, 0, 0, 0, 0);
    }

    public MobilePoint(double x, double y,//
    		double dx, double dy, //
    		double ddx, double ddy) {
        setPositionX(x);
        setPositionY(y);
        setSpeedX(dx);
        setSpeedY(dy);
        setAccelerationX(ddx);
        setAccelerationY(ddy);
    }

    public MobilePoint(final IPhysicalPoint physicalPoint) {
        setPositionX(physicalPoint.getPositionX());
        setPositionY(physicalPoint.getPositionY());
        setSpeedX(physicalPoint.getSpeedX());
        setSpeedY(physicalPoint.getSpeedY());
        setAccelerationX(physicalPoint.getAccelerationX());
        setAccelerationY(physicalPoint.getAccelerationY());
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
        return dx;
    }

    @Override
    public double getSpeedY() {
        return dy;
    }

    @Override
    public double getAccelerationX() {
        return ddx;
    }

    @Override
    public double getAccelerationY() {
        return ddy;
    }

    @Override
    public boolean setPositionX(double x) {
        this.x = x;
        return true;
    }

    @Override
    public boolean setPositionY(double y) {
        this.y = y;
        return true;
    }

    @Override
    public boolean setSpeedX(double dx) {
        this.dx = dx;
        return true;
    }

    @Override
    public boolean setSpeedY(double dy) {
        this.dy = dy;
        return true;
    }

    @Override
    public boolean setAccelerationX(double ddx) {
        this.ddx = ddx;
        return true;
    }

    @Override
    public boolean setAccelerationY(double ddy) {
        this.ddy = ddy;
        return true;
    }

    @Override
    public IPhysicalPoint copy() {
        return new MobilePoint(x, y, dx, dy, ddx, ddy);
    }

}
