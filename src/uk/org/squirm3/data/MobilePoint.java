package uk.org.squirm3.data;

/**  
Copyright 2007 Bertrand Dechoux

This file is part of Organic Builder.

Organic Builder is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Organic Builder is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Organic Builder; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public class MobilePoint implements IPhysicalPoint {

	private float x, y, dx, dy, ddx, ddy;
	
	public MobilePoint() {
		this(0, 0, 0, 0, 0, 0);
	}
	
	public MobilePoint(float x, float y,
			float dx, float dy, float ddx, float ddy) {
		setPositionX(x);
		setPositionY(y);
		setSpeedX(dx);
		setSpeedY(dy);
		setAccelerationX(ddx);
		setAccelerationY(ddy);
	}
	
	public MobilePoint(IPhysicalPoint physicalPoint) {
		setPositionX(physicalPoint.getPositionX());
		setPositionY(physicalPoint.getPositionY());
		setSpeedX(physicalPoint.getSpeedX());
		setSpeedY(physicalPoint.getSpeedY());
		setAccelerationX(physicalPoint.getAccelerationX());
		setAccelerationY(physicalPoint.getAccelerationY());
	}

	public float getPositionX() { return x; }
	public float getPositionY() { return y; }
	public float getSpeedX() { return dx; }
	public float getSpeedY() { return dy; }
	public float getAccelerationX() { return ddx; }
	public float getAccelerationY() { return ddy; }

	public boolean setPositionX(float x) {
		this.x = x;
		return true;
	}

	public boolean setPositionY(float y) {
		this.y = y;
		return true;
	}

	public boolean setSpeedX(float dx) {
		this.dx = dx;
		return true;
	}

	public boolean setSpeedY(float dy) {
		this.dy = dy;
		return true;
	}

	public boolean setAccelerationX(float ddx) {
		this.ddx = ddx;
		return true;
	}

	public boolean setAccelerationY(float ddy) {
		this.ddy = ddy;
		return true;
	}

	public IPhysicalPoint copy() {
		return new MobilePoint(x, y, dx, dy, ddx, ddy);
	}

}
