package uk.org.squirm3.data;
/**
<p>Copyright :  Copyright (C) 2005 Tim J. Hutton - tim.hutton@gmail.com - http://www.sq3.org.uk</p>
<p>License : GNU GENERAL PUBLIC LICENSE </p>

<p>This program is free software; you can redistribute it and/or modifyit under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.</p>

<p>This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.</p>

<p>You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA</p>

<p>Full text of GPL license can be found either at the bottom of the README.txt file or at www.gnu.org/copyleft/gpl.html</p>
*/


public abstract class Level
{			
	private final String title, challenge, hint;
		
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
		Atom[] newAtoms = new Atom[numberOfAtoms];
		float atomSize = Atom.getAtomSize();
		int side = (int)Math.ceil(Math.sqrt(numberOfAtoms));
		int spacing_x=(int)(width/(side+2));
		int spacing_y=(int)(height/(side+2));
		
		for (int i=0;i<newAtoms.length;i++) {
			int row = (int)Math.floor(i/(float)side);
			int column = i%side;
			newAtoms[i] = new Atom((row+1)*spacing_x,(column+1)*spacing_y,i%6,0,atomSize/3);
		}
		
		/* older code: used to scatter at random (is ok for old physics, causes excess energy in new)
		for(int i=0;i<newAtoms.length;i++)
			atoms[i] = new sq3Atom(((float)Math.random()*w),((float)Math.random()*h),i%6,0,MAX_SPEED);
		*/
		
		return newAtoms;
	
	}
}