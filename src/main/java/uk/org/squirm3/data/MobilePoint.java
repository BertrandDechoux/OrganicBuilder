package uk.org.squirm3.data;

public class MobilePoint implements IPhysicalPoint {

    private float x, y, dx, dy, ddx, ddy;

    public MobilePoint() {
        this(0, 0, 0, 0, 0, 0);
    }

    public MobilePoint(final float x, final float y, final float dx,
            final float dy, final float ddx, final float ddy) {
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

    public float getPositionX() {
        return x;
    }

    public float getPositionY() {
        return y;
    }

    public float getSpeedX() {
        return dx;
    }

    public float getSpeedY() {
        return dy;
    }

    public float getAccelerationX() {
        return ddx;
    }

    public float getAccelerationY() {
        return ddy;
    }

    public boolean setPositionX(final float x) {
        this.x = x;
        return true;
    }

    public boolean setPositionY(final float y) {
        this.y = y;
        return true;
    }

    public boolean setSpeedX(final float dx) {
        this.dx = dx;
        return true;
    }

    public boolean setSpeedY(final float dy) {
        this.dy = dy;
        return true;
    }

    public boolean setAccelerationX(final float ddx) {
        this.ddx = ddx;
        return true;
    }

    public boolean setAccelerationY(final float ddy) {
        this.ddy = ddy;
        return true;
    }

    public IPhysicalPoint copy() {
        return new MobilePoint(x, y, dx, dy, ddx, ddy);
    }

}
