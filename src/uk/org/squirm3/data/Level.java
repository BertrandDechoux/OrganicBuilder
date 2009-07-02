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

public abstract class Level {
	public static final int[] TYPES = {0, 1, 2, 3, 4, 5};
	
	private final String title, challenge, hint;
	private final String[] errors;
	private Configuration configuration;
	private final Configuration defaultConfiguration;

	public Level(String title, String challenge, String hint, String[] errors,
			Configuration defaultConfiguration){
		this.title = title;
		this.challenge = challenge; 
		this.hint = hint;
		this.errors = new String[errors.length];
		System.arraycopy(errors, 0,
                this.errors, 0, this.errors.length);
		this.defaultConfiguration = defaultConfiguration;
	}
	
	final public Atom[] createAtoms(Configuration configuration) {
		if(configuration==null) {
			return createAtoms_internal(getConfiguration());
		}
		Atom[] atoms = createAtoms_internal(configuration);
		if(atoms!=null) setConfiguration(configuration);
		return atoms;
	}
	
	protected abstract Atom[] createAtoms_internal(Configuration configuration);
	
	public abstract String evaluate(Atom[] atoms);
	
	public String getTitle() { return title; }
	public String getChallenge() { return challenge; }
	public String getHint() { return hint; }
	public Configuration getDefaultConfiguration() { return defaultConfiguration; }
	public Configuration getConfiguration() {
		return (configuration==null)?defaultConfiguration:configuration;
	}
	
	protected String getError(int number) {
		return errors[number-1]; // indices in level*.properties are one-based
	}
	
	protected void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	protected static void setRandomSpeed(IPhysicalPoint iPhysicalPoint) {
		final float ms = Atom.getAtomSize()/3;
		iPhysicalPoint.setSpeedX((float)(Math.random()*ms-ms/2.0));
		iPhysicalPoint.setSpeedY((float)(Math.random()*ms-ms/2.0));
	}

	protected static boolean createAtoms(int numberOfAtoms, int[] types,
			float x0, float x1, float y0, float y1, Atom[] atoms) {
		if(types.length <1 || numberOfAtoms > atoms.length) return false;
		final float atomSize = Atom.getAtomSize();
		
		// check that enough space will be let to allow clean reactions
		final int evaluation = (int)((x1-x0)/(atomSize*3)) * (int)((y1-y0)/(atomSize*3));
		if(evaluation<numberOfAtoms) return false;
		
		// creation of the atoms
		final IPhysicalPoint iPhysicalPoint = new MobilePoint();
		int n= atoms.length-numberOfAtoms;
		
		for(float x = x0 + 2*atomSize; x < x1-2*atomSize && n< atoms.length ; x += 3*atomSize)
			for(float y = y0 + 2*atomSize; y < y1-2*atomSize && n < atoms.length ; y += 3*atomSize) {
				iPhysicalPoint.setPositionX(x);
				iPhysicalPoint.setPositionY(y);
				setRandomSpeed(iPhysicalPoint);
				atoms[n] = new Atom(iPhysicalPoint,types[n%types.length],0);
				n++;
			}
		return true;
	}
}
