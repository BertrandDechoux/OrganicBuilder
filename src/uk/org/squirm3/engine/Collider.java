package uk.org.squirm3.engine;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.DraggingPointData;
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
along with Foobar; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/


public class Collider
{	
	// ------- data ---------

	private Atom atoms[] = new Atom[0];
	private int n_atoms = 0;
	private Vector reactions;
	
	// structures for space-division speed-up:
	private List buckets[][]; // each bucket has a list of the indices of the sq3Atoms contained within it
	private int n_buckets_x=0,n_buckets_y=0; // the horizontal and vertical dimensions are divided into this many buckets
	private int width;
	private int height;
	// we store the size to ensure we access the right bucket (dimensions might change)
	private float bucket_width,bucket_height; 

	// some things specific to the older cheaper physics
	private float MAX_SPEED=5.0f; // recomputed when R changes (thanks Ralph)

	// some things for the new more accurate physics
	private final double dt=0.01; // the time interval
	private final double halfdt=0.5*dt;
	private final double halfdt2=0.5*dt*dt;
	private double WORLD_SCALE=1.8; // simply determines how zoomed-in we are

	// ------- methods ---------
	
	public Collider(int number_of_atoms,int w,int h)
	{
		reactions = new Vector();
		width = -1;
		height = -1;
		// indicating uninitialised
		n_atoms = number_of_atoms;
		// recompute MAX_SPEED to allow for the new R
		float R = Atom.getAtomSize();
		MAX_SPEED = 5.0f*R/22.0f; // (thanks Ralph)
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
	private synchronized void refreshBuckets(int w,int h)
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
	
	private synchronized int whichBucketX(float x)
	{
		int w = (int)Math.floor(x/bucket_width);
		if(w<0) w=0;
		else if(w>=n_buckets_x) w=n_buckets_x-1;
		return w;
	}
	
	private synchronized int whichBucketY(float y)
	{
		int w = (int)Math.floor(y/bucket_height);
		if(w<0) w=0;
		else if(w>=n_buckets_y) w=n_buckets_y-1;
		return w;
	}
	
	private synchronized void insertAtomIntoBucket(int i)
	{
		Atom a = atoms[i];
		int bucket_x,bucket_y;
		bucket_x = whichBucketX(a.pos.x);
		bucket_y = whichBucketY(a.pos.y);
		buckets[bucket_x][bucket_y].add(new Integer(i));
	}
	public synchronized void doTimeStep(int width, int height, DraggingPointData draggingPointData) {
		if(draggingPointData==null) doTimeStep(width, height, false , 0, 0, 0);
		else doTimeStep(width, height, true , draggingPointData.getWhichBeingDragging(),
				(int)draggingPointData.getX(), (int)draggingPointData.getY());
	}


	public synchronized void doTimeStep(int width, int height,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y) {
		// which physics engine do you want to use?
		if(true)
		{
			// straight Euler method computation of spring forces per timestep
			// uses a maximum speed limiter to prevent numerical problems
			// doesn't lose overall speed however, since force computation typically overestimates anyway
			// but reliable and quick
			// disadvantage: bonded atom groups lose group momentum as speed limiter kicks in
			doTimeStep1(width, height,is_dragging,which_being_dragged,mouse_x,mouse_y);
		}
		else
		{
			// uses the Verlet algorithm to simulate N particles, repelling each other
			// via the Lennard-Jones potential, following Chapter 8 of:
			// An Introduction to Computer Simulation Methods: Preliminary third edition
			// by: Harvey Gould, Jan Tobochnik, and Wolfgang Christian
			// chapters downloadable at: http://sip.clarku.edu/3e/ (thankyou!)
			// no speed limiting, hence potentially better-looking physics
			// disadvantage: numericallly unstable: Boltzmann distribution of speeds allows
			// for occasional high-speed atoms which can cause an escalating cascade of numerical errors,
			// and atoms fly away like crazy. maybe this issue is resolvable without losing benefits
			// disadvantage: slower to compute than simple spring forces
			doTimeStep2(width, height,is_dragging,which_being_dragged,mouse_x,mouse_y);
		}
		// some possible alternative physics:
		// - a hard-sphere type physics, where instead of a constant timestep we search for future
		//   collisions between the spheres and run the sim forward to that point, and recompute their 
		//   velocities as a result of the collision. might be promising to explore for OB - but how to include
		//   bonds (and dragging)?
		// - a lattice-based physics can run very fast indeed but doesn't look as satisfying
	}

	private synchronized void doTimeStep1(int width, int height,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y){
		recomputeVelocitiesAndReact(width, height,is_dragging,which_being_dragged,mouse_x,mouse_y);
		moveAtoms();
	}

	
	// used in doTimeStep1
	private synchronized void recomputeVelocitiesAndReact(int width, int height,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y)
	{
		// exception fix, to be removed !!! there is no change of atoms number....
		
		// old code before we started experimenting with the proper way to do the physics...

		// if the size has changed, refresh the buckets
		if(width != this.width || height != this.height)
			refreshBuckets(width,height);
		
		// we first shuffle the atoms list in order to prevent any reaction artefacts, since
		// reactions are applied to atom-pairs as they are found
		// NB. this isn't necessary for the physics since the forces are computed for all atoms
		// before being applied
		//Collections.shuffle(Arrays.asList(atoms));
		// NB we quickly stopped doing this because it wrecked dragging in a non-easily-fixable way... thanks Henrik
		
		// we also shuffle the reactions list in order to prevent any reaction artefacts, since
		// reactions are applied as they are found, and conflicting reactions would only ever have
		// the first one applied, eg. with a1c1->a2c2 and x1c1->x4c4, only the first version would apply
		// to any a1c1 pairs. Now each reaction has an equal chance of being chosen.
		Collections.shuffle(reactions);
		
		// starting over for this iteration
		for(int i=0;i<atoms.length;i++)
			atoms[i].has_reacted=false;
		
		float R = Atom.getAtomSize();
		float diam = 2.0f*R;
		float diam2 = diam*diam; 

		for(int i=0;i<atoms.length;i++)
		{
			Atom a = atoms[i];
			// bounce off the walls
			if(a.pos.x<R)
				a.velocity.x += getForce(R-a.pos.x);
			if(a.pos.y<R)
				a.velocity.y += getForce(R-a.pos.y);
			if(a.pos.x>width-R)
				a.velocity.x -= getForce(a.pos.x-(width-R));
			if(a.pos.y>height-R)
				a.velocity.y -= getForce(a.pos.y-(height-R));
			// bounce off other atoms that are within 2R distance of this one
			// what square radius must we search for neighbours?
			int rx = (int)Math.ceil(diam/bucket_width);
			int ry = (int)Math.ceil(diam/bucket_height);
			// what bucket is the atom in?
			int wx = whichBucketX(a.pos.x);
			int wy = whichBucketY(a.pos.y);
			// accumulate the list of any atoms in this square radius (clamped to the valid area)
			for(int x=Math.max(0,wx-rx);x<=Math.min(n_buckets_x-1,wx+rx);x++)
			{
				for(int y=Math.max(0,wy-ry);y<=Math.min(n_buckets_y-1,wy+ry);y++)
				{
					// add each atom that is in this bucket
					Iterator it = buckets[x][y].listIterator();
					while(it.hasNext())
					{
						int iOther = ((Integer)it.next()).intValue();
						if(iOther<=i) continue; // using Newton's "action&reaction" as a shortcut
						Atom b = atoms[iOther];
						if(a.pos.distanceSq(b.pos)<diam2) 
						{
							// this is a collision - can any reactions apply to these two atoms?
							Reaction.tryReaction(a,b, reactions);
							// atoms bounce off other atoms
							float sep = (float)a.pos.distance(b.pos);
							float force = getForce(diam-sep);
							// push from the other atom
							float dx = force * (a.pos.x - b.pos.x)/sep;
							float dy = force * (a.pos.y - b.pos.y)/sep;
							a.velocity.x += dx;
							a.velocity.y += dy;
							b.velocity.x -= dx; // using Newton's "action&reaction" as a shortcut
							b.velocity.y -= dy;
						}
					}
				}
			}
			// bonds act like springs
			Iterator it = a.bonds.iterator();
			while(it.hasNext()) {
				Atom other =(Atom)it.next();
				float sep = (float)a.pos.distance(other.pos);
				float force = getForce(sep-diam)/4.0f; // this determines the bond spring stiffness
				// pull towards the other atom
				float dx = force * (other.pos.x - a.pos.x)/sep;
				float dy = force * (other.pos.y - a.pos.y)/sep;
				a.velocity.x += dx;
				a.velocity.y += dy;
			}
			// the user can pull atoms about using the mouse
			if(is_dragging && which_being_dragged==i)
			{
				// normalise the pull vector
				float pullX = mouse_x-a.pos.x;
				float pullY = mouse_y-a.pos.y;
				float dist = (float)Math.sqrt(pullX*pullX+pullY*pullY);
				pullX /= dist;
				pullY /= dist;
				a.velocity.x += 2.0f*pullX;
				a.velocity.y += 2.0f*pullY;
				
			}
			// limit the velocity of each atom to prevent numerical problems
			float speed = (float)Math.sqrt(a.velocity.x*a.velocity.x + a.velocity.y*a.velocity.y);
			if(speed>MAX_SPEED)
			{
				a.velocity.x *= MAX_SPEED/speed;
				a.velocity.y *= MAX_SPEED/speed;
			}
		}
	}

	// used in doTimeStep1
	private synchronized void moveAtoms()
	{
		for(int i=0;i<atoms.length;i++)
		{
			Atom a = atoms[i];
			if(a.stuck) continue; // special atoms that don't move
			
			int current_bucket_x,current_bucket_y;
			current_bucket_x = whichBucketX(a.pos.x);
			current_bucket_y = whichBucketY(a.pos.y);
			
			a.pos.x+=atoms[i].velocity.x;
			a.pos.y+=atoms[i].velocity.y;
			
			int new_bucket_x,new_bucket_y;
			new_bucket_x = whichBucketX(a.pos.x);
			new_bucket_y = whichBucketY(a.pos.y);
			
			// do we need to move the atom to a new bucket?
			if(new_bucket_x!=current_bucket_x || new_bucket_y!=current_bucket_y)
			{
				// remove the atom index from the list
				java.util.List list = buckets[current_bucket_x][current_bucket_y];
				ListIterator it = list.listIterator(0);
				while(it.hasNext())
				{
					if(((Integer)it.next()).intValue()==i)
						it.remove();
				}
				buckets[new_bucket_x][new_bucket_y].add(new Integer(i));
			}
		}
	}
	
	// used in doTimeStep1
	private float getForce(float d)
	{
		float R = Atom.getAtomSize();
		return 1.0f*d*22.0f/R; // what is the overlap/overstretch force for distance d?
		// (now inversely proportional to R, thanks Ralph)
	}
	
	// the newer physics: more accurate atomic interactions but problems with stability and cost
	private synchronized void doTimeStep2(int width, int height ,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y)
	{
		// if the size has changed, refresh the buckets
		if(width != this.width || height != this.height)
			refreshBuckets(width,height);
		
		// new version being tried out with improved physics
		// computes velocity in two steps using old and new acceleration (the Verlet algorithm)
		float R = Atom.getAtomSize();
		for(int i=0;i<atoms.length;i++)  // use acceleration previously computed (zero at start)
		{
			Atom a = atoms[i];

			int current_bucket_x,current_bucket_y;
			current_bucket_x = whichBucketX(a.pos.x);
			current_bucket_y = whichBucketY(a.pos.y);
			
			// update the atom's position
			a.pos.x += WORLD_SCALE*R * (a.velocity.x * dt + a.acceleration.x * halfdt2); // halfdt2 = 0.5*dt*dt
			a.pos.y += WORLD_SCALE*R * (a.velocity.y * dt + a.acceleration.y * halfdt2);
			
			int new_bucket_x,new_bucket_y;
			new_bucket_x = whichBucketX(a.pos.x);
			new_bucket_y = whichBucketY(a.pos.y);
			// do we need to move the atom to a new bucket?
			if(new_bucket_x!=current_bucket_x || new_bucket_y!=current_bucket_y)
			{
				// remove the atom index from the list
				java.util.List list = buckets[current_bucket_x][current_bucket_y];
				ListIterator it = list.listIterator(0);
				while(it.hasNext())
				{
					if(((Integer)it.next()).intValue()==i)
						it.remove();
				}
				buckets[new_bucket_x][new_bucket_y].add(new Integer(i));
			}

			a.velocity.x += a.acceleration.x * halfdt; // add old acceleration , halfdt = 0.5*dt
			a.velocity.y += a.acceleration.y * halfdt;
		}
		computeAcceleration(width, height,is_dragging,which_being_dragged,mouse_x,mouse_y);
		for (int i = 0; i<atoms.length;i++)  // add new acceleration
		{
			Atom a = atoms[i];
			a.velocity.x += a.acceleration.x * halfdt;
			a.velocity.y += a.acceleration.y * halfdt;
		}		
	}

	// used in doTimeStep2
	// doesn't yet allow reactions, or bonds. Has severe problems with stability.
	private synchronized void computeAcceleration(int width, int height ,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y)
	{
		float R = Atom.getAtomSize();
		// set the accelerations for this iteration to zero
		for(int i=0;i<atoms.length;i++) 
		{
			atoms[i].acceleration.x = 0;
			atoms[i].acceleration.y = 0;
		}
		for(int i=0;i<atoms.length-1;i++)
		{
			Atom a = atoms[i];
			// what square radius must we search for neighbours?
			double radius = 2.5*R;
			int rx = (int)Math.ceil(radius/bucket_width);
			int ry = (int)Math.ceil(radius/bucket_height);
			// what bucket is the atom in?
			int wx = whichBucketX(a.pos.x);
			int wy = whichBucketY(a.pos.y);
			// accumulate the list of any atoms in this square radius (clamped to the valid area)
			for(int x=Math.max(0,wx-rx);x<=Math.min(n_buckets_x-1,wx+rx);x++)
			{
				for(int y=Math.max(0,wy-ry);y<=Math.min(n_buckets_y-1,wy+ry);y++)
				{
					// add each atom that is in this bucket
					Iterator it = buckets[x][y].listIterator();
					while(it.hasNext())
					{
						int iOther = ((Integer)it.next()).intValue();
						if(iOther<=i) continue; // action-reaction
						Atom b = atoms[iOther];
						double dx = a.pos.x-b.pos.x;
						double dy = a.pos.y-b.pos.y;
						dx /= WORLD_SCALE*R;
						dy /= WORLD_SCALE*R;
						double r2 = dx*dx+dy*dy;
						if(r2<2.5*R*2.5*R) // no need to compute forces over vast distances
						{
							double oneOverR2 = 1.0/r2;
							double oneOverR6 = oneOverR2*oneOverR2*oneOverR2;
							double fOverR = 48.0*oneOverR6*(oneOverR6*0.5)*oneOverR2;
							double fx = fOverR*dx;
							double fy = fOverR*dy;
							a.acceleration.x += fx;
							a.acceleration.y += fy;
							b.acceleration.x -= fx;
							b.acceleration.y -= fy;
							// (could include reactions checking here, but haven't done this due to other 
							//  problems with this type of physics)
						}
					}
				}
			}
		}
		// also include the effect of the walls
		for(int i=0;i<atoms.length;i++) 
		{
			Atom a = atoms[i];
			for(int wall=0;wall<4;wall++)
			{
				double dx=0,dy=0;
				switch(wall) {
					case 0: dx=a.pos.x; break;
					case 1: dx=a.pos.x-width; break;
					case 2: dy=a.pos.y; break;
					case 3: dy=a.pos.y-height; break;
				}
				dx /= WORLD_SCALE*R;
				dy /= WORLD_SCALE*R;
				double r2 = dx*dx+dy*dy;
				if(r2<2.5*R*2.5*R)
				{
					double oneOverR2 = 1.0/r2;
					double oneOverR6 = oneOverR2*oneOverR2*oneOverR2;
					double fOverR = 48.0*oneOverR6*(oneOverR6*0.5)*oneOverR2;
					double fx = fOverR*dx;
					double fy = fOverR*dy;
					a.acceleration.x += fx;
					a.acceleration.y += fy;
				}
			}
		}
		// the user can pull atoms about using the mouse (causes a problem)
		if(is_dragging)
		{
			Atom a = atoms[which_being_dragged];
			// normalise the pull vector
			float pullX = mouse_x-a.pos.x;
			float pullY = mouse_y-a.pos.y;
			float dist = (float)Math.sqrt(pullX*pullX+pullY*pullY);
			pullX /= dist;
			pullY /= dist;
			a.acceleration.x += 3.0*R*pullX;
			a.acceleration.y += 3.0*R*pullY;
		}
	}
}