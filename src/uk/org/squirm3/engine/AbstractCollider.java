package uk.org.squirm3.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.DraggingPoint;
import uk.org.squirm3.data.Reaction;


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


public abstract class AbstractCollider
{	
	// ------- data ---------

	protected Atom atoms[] = new Atom[0];
	protected int n_atoms = 0;
	protected Vector reactions;
	
	// structures for space-division speed-up:
	protected List buckets[][]; // each bucket has a list of the indices of the sq3Atoms contained within it
	protected int n_buckets_x=0,n_buckets_y=0; // the horizontal and vertical dimensions are divided into this many buckets
	protected int width;
	protected int height;
	// we store the size to ensure we access the right bucket (dimensions might change)
	protected float bucket_width,bucket_height;

	// ------- methods ---------
	
	public AbstractCollider(int number_of_atoms,int w,int h)
	{
		reactions = new Vector();
		width = -1;
		height = -1;
		// indicating uninitialised
		n_atoms = number_of_atoms;
	}

	public synchronized Atom[] getAtoms() {
		return copyArray(atoms);
	}
	
	public synchronized Vector getReactions() {
		/*Vector copy = new Vector(reactions.size());
		Iterator it = copy.iterator();
		while(it.hasNext()) copy.add(it.next());
		return copy;*/
		return reactions;
	}
	
	public void setReactions(final Reaction[] newReactions) {
		Vector reactions = new Vector(newReactions.length);
		for(int i = 0 ; i < newReactions.length ; i++)
			reactions.add(newReactions[i]);
		this.reactions = reactions;
	}
	
	public synchronized void setAtoms(Atom[] newAtoms, int w, int h) {
		atoms= copyArray(newAtoms);
		n_atoms=atoms.length;
		refreshBuckets(w,h);
	}
	
	// should be in sq3atom class
	private Atom[] copyArray(Atom[] original){
		Atom[] copy = new Atom[original.length];
		for(int i=0; i<copy.length ; i++)
			copy[i]=original[i]; //.clone() : TODO => true copy
		return copy;
	}

	public synchronized int getNumAtoms()
	{
		return n_atoms;
	}
	
	public synchronized void setNAtoms(int NAtoms){
		n_atoms = NAtoms;
	}
	
	// something has changed (init/resize/reset/R_change) - recreate the buckets structure
	// (synchronized so that other functions don't find the buckets changing under their feet mid-loop)
	protected synchronized void refreshBuckets(int w,int h)
	{
		// size the buckets structure so each bucket is approximately R in size (approx 1 atom per bucket)
		float R = Atom.getAtomSize();
		n_buckets_x = Math.round( (float)w / (1.0f*R) ); 
		n_buckets_y = Math.round( (float)h / (1.0f*R) ); 

		if(n_buckets_x>0 && n_buckets_y>0) // (else div0 error)
		{
			buckets = new LinkedList[n_buckets_x][n_buckets_y]; // (garbage collection takes care of old array)
			// allocate each
			for(int x=0;x<n_buckets_x;x++) for(int y=0;y<n_buckets_y;y++) buckets[x][y] = new LinkedList();
			width = w;
			height = h;
			bucket_width = w / (float)n_buckets_x;
			bucket_height = h / (float)n_buckets_y;
			// insert any atoms currently present
			for(int i=0;i<atoms.length;i++)
				insertAtomIntoBucket(i);
		}
	}
	
	protected synchronized int whichBucketX(float x)
	{
		int w = (int)Math.floor(x/bucket_width);
		if(w<0) w=0;
		else if(w>=n_buckets_x) w=n_buckets_x-1;
		return w;
	}
	
	protected synchronized int whichBucketY(float y)
	{
		int w = (int)Math.floor(y/bucket_height);
		if(w<0) w=0;
		else if(w>=n_buckets_y) w=n_buckets_y-1;
		return w;
	}
	
	protected synchronized void insertAtomIntoBucket(int i)
	{
		Atom a = atoms[i];
		int bucket_x,bucket_y;
		bucket_x = whichBucketX(a.pos.x);
		bucket_y = whichBucketY(a.pos.y);
		buckets[bucket_x][bucket_y].add(new Integer(i));
	}

	public synchronized void doTimeStep(int width, int height, DraggingPoint draggingPoint) {
		if(draggingPoint==null) doTimeStep(width, height, false , 0, 0, 0);
		else doTimeStep(width, height, true , draggingPoint.getWhichBeingDragging(),
				(int)draggingPoint.getX(), (int)draggingPoint.getY());
	}


	public abstract void doTimeStep(int width, int height,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y);
}