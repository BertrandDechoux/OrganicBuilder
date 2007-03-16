package uk.org.squirm3.data;

public abstract class Level
{			
	private final String title, challenge, hint;
	private byte id; // keep the number of this level
	
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

	public byte getId() {
		return id;
	}

	public void setId(byte id) {
		this.id = id;
	}
}