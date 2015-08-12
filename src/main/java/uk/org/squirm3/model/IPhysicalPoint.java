package uk.org.squirm3.model;

public interface IPhysicalPoint {
    double getPositionX();
    double getPositionY();

    double getSpeedX();
    double getSpeedY();

    double getAccelerationX();
    double getAccelerationY();

    boolean setPositionX(double x);
    boolean setPositionY(double y);

    boolean setSpeedX(double dx);
    boolean setSpeedY(double dy);

    boolean setAccelerationX(double ddx);
    boolean setAccelerationY(double ddy);

    public IPhysicalPoint copy(); // TODO use clone, with generic ?
}
