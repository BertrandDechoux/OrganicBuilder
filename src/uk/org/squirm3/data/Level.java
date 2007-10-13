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

public abstract class Level
{
	public final int[] TYPES = {0, 1, 2, 3, 4, 5};
	private final String title, challenge, hint;
	private byte id; // keep the number of this level
	//TODO remove : should not be coded like that
	// the element of the collection should not know where it is located
	
	public Level(String title, String challenge, String hint){
		this.title = title;
		this.challenge = challenge; 
		this.hint = hint;
	}
	
	public abstract Atom[] resetAtoms(int numberOfAtoms, int width, int height); 
	public abstract String evaluate(Atom[] atoms);
	
	public String getTitle() {
		return title;
	}
	
	public String getChallenge() {
		return challenge;
	}
	
	public String getHint() {
		return hint;
	}
	
	protected static Atom[] getDefaultReset(int numberOfAtoms, int width, int height){
		final Atom[] newAtoms = new Atom[numberOfAtoms];
		final float atomSize = Atom.getAtomSize();
		final int side = (int)Math.ceil(Math.sqrt(numberOfAtoms));
		final int spacing_x=(int)(width/(side+2));
		final int spacing_y=(int)(height/(side+2));
		final IPhysicalPoint iPhysicalPoint = new MobilePoint();
		final float ms = atomSize/3;

		for (int i=0;i<newAtoms.length;i++) {
			int row = (int)Math.floor(i/(float)side);
			int column = i%side;
			iPhysicalPoint.setPositionX((row+1)*spacing_x);
			iPhysicalPoint.setPositionY((column+1)*spacing_y);
			iPhysicalPoint.setSpeedX((float)(Math.random()*ms-ms/2.0));
			iPhysicalPoint.setSpeedY((float)(Math.random()*ms-ms/2.0));
			newAtoms[i] = new Atom(iPhysicalPoint,i%6,0);
		}
		
		/* older code: used to scatter at random (is ok for old physics, causes excess energy in new)
		for(int i=0;i<newAtoms.length;i++)
			atoms[i] = new sq3Atom(((float)Math.random()*w),((float)Math.random()*h),i%6,0,MAX_SPEED);
		*/
		
		return newAtoms;
	
	}
	
	protected static boolean createAtoms(int numberOfAtoms, int[] types, float x0, float x1, float y0, float y1, Atom[] atoms) {
		if(types.length <1 || numberOfAtoms > atoms.length) return false;
		final float atomSize = Atom.getAtomSize();
		
		// check that enough space will be let to allow clean reactions
		final int evaluation = (int)((x1-x0)/(atomSize*3)) * (int)((y1-y0)/(atomSize*3));
		if(evaluation<numberOfAtoms) return false;
		
		// creation of the atoms
		final IPhysicalPoint iPhysicalPoint = new MobilePoint();
		final float ms = atomSize/3;
		int n= atoms.length-numberOfAtoms;
		
		for(float x = x0 + 2*atomSize; x < x1-2*atomSize && n< atoms.length ; x += 3*atomSize)
			for(float y = y0 + 2*atomSize; y < y1-2*atomSize && n < atoms.length ; y += 3*atomSize) {
				iPhysicalPoint.setPositionX(x);
				iPhysicalPoint.setPositionY(y);
				iPhysicalPoint.setSpeedX((float)(Math.random()*ms-ms/2.0));
				iPhysicalPoint.setSpeedY((float)(Math.random()*ms-ms/2.0));
				atoms[n] = new Atom(iPhysicalPoint,types[n%types.length],0);
				n++;
			}
		return true;
	}

	public byte getId() {
		return id;
	}

	public void setId(byte id) {
		this.id = id;
	}
}