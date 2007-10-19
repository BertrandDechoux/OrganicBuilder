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

public final class Configuration {
	private final int numberOfAtoms;
	private final float width, height;
	private final int[] types;

	public Configuration(int numberOfAtoms, int[] types,
			float width, float height) {
		this.numberOfAtoms = numberOfAtoms;
		int[] types_copy = new int[types.length];
		System.arraycopy(types, 0, types_copy, 0, types.length);
		this.types = types_copy;
		this.width = width;
		this.height = height;
	}
	
	public int getNumberOfAtoms() { return numberOfAtoms; }
	public float getWidth() { return width; }
	public float getHeight() { return height; }
	public int[] getTypes() { return types; }
}
