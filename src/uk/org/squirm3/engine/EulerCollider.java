package uk.org.squirm3.engine;

import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import uk.org.squirm3.data.Atom;
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


public class EulerCollider extends AbstractCollider
{	
	private float MAX_SPEED=5.0f; // recomputed when R changes (thanks Ralph)

	// ------- methods ---------
	
	public EulerCollider(int number_of_atoms,int w,int h) {
		super(number_of_atoms, w, h);
		// recompute MAX_SPEED to allow for the new R
		float R = Atom.getAtomSize();
		MAX_SPEED = 5.0f*R/22.0f; // (thanks Ralph)
	}

	public synchronized void doTimeStep(int width, int height,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y) {
		// straight Euler method computation of spring forces per timestep
		// uses a maximum speed limiter to prevent numerical problems
		// doesn't lose overall speed however, since force computation typically overestimates anyway
		// but reliable and quick
		// disadvantage: bonded atom groups lose group momentum as speed limiter kicks in
		recomputeVelocitiesAndReact(width, height,is_dragging,which_being_dragged,mouse_x,mouse_y);
		moveAtoms();
		// some possible alternative physics:
		// - a hard-sphere type physics, where instead of a constant timestep we search for future
		//   collisions between the spheres and run the sim forward to that point, and recompute their 
		//   velocities as a result of the collision. might be promising to explore for OB - but how to include
		//   bonds (and dragging)?
		// - a lattice-based physics can run very fast indeed but doesn't look as satisfying
	}
	
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
	
	private float getForce(float d)
	{
		float R = Atom.getAtomSize();
		return 1.0f*d*22.0f/R; // what is the overlap/overstretch force for distance d?
		// (now inversely proportional to R, thanks Ralph)
	}
}