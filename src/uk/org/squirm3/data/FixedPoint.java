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

	public float getPositionX() { return x; }
	public float getPositionY() { return y; }
	public float getSpeedX() { return 0; }
	public float getSpeedY() { return 0; }
	public float getAccelerationX() { return 0; }
	public float getAccelerationY() { return 0; }

	public boolean setPositionX(float x) { return false; }
	public boolean setPositionY(float y) { return false; }
	public boolean setSpeedX(float dx) { return false; }
	public boolean setSpeedY(float dy) { return false; }
	public boolean setAccelerationX(float ddx) { return false; }
	public boolean setAccelerationY(float ddy) { return false; }

	public IPhysicalPoint copy() { return this; }
}
