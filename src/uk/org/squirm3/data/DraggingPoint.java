package uk.org.squirm3.data;

/**  
Copyright 2007 Tim J. Hutton, Ralph Hartley, Bertrand Dechoux

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

public class DraggingPoint {
	private final long x;
	private final long y;
	private final int whichBeingDragging;
	
	public DraggingPoint(long x, long y, int whichBeingDragging) {
		this.x = x;
		this.y = y;
		this.whichBeingDragging = whichBeingDragging;
	}

	public int getWhichBeingDragging() {
		return whichBeingDragging;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DraggingPoint other = (DraggingPoint) obj;
		if (whichBeingDragging != other.whichBeingDragging)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
