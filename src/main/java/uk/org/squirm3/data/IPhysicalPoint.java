package uk.org.squirm3.data;

/**
 * ${my.copyright}
 */

public interface IPhysicalPoint {
    public float getPositionX();

    public float getPositionY();

    public float getSpeedX();

    public float getSpeedY();

    // acceleration only used in new correct physics code
    public float getAccelerationX();

    public float getAccelerationY();

    public boolean setPositionX(float x);

    public boolean setPositionY(float y);

    public boolean setSpeedX(float dx);

    public boolean setSpeedY(float dy);

    public boolean setAccelerationX(float ddx);

    public boolean setAccelerationY(float ddy);

    public IPhysicalPoint copy(); //TODO use clone, with generic ?
}
