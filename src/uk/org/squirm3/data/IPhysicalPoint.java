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

public interface IPhysicalPoint {
	public float getPositionX();
	public float getPositionY();
	public float getSpeedX();
	public float getSpeedY();
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
