package uk.org.squirm3.engine;

import java.util.Iterator;
import java.util.ListIterator;

import uk.org.squirm3.data.Atom;


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


public class VerletCollider extends AbstractCollider
{

	// some things for the new more accurate physics
	private final double dt=0.01; // the time interval
	private final double halfdt=0.5*dt;
	private final double halfdt2=0.5*dt*dt;
	private double WORLD_SCALE=1.8; // simply determines how zoomed-in we are

	// ------- methods ---------
	
	public VerletCollider(int number_of_atoms,int w,int h) {
		super(number_of_atoms, w, h);
	}

	public synchronized void doTimeStep(int width, int height,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y) {
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
			current_bucket_x = whichBucketX(a.getPhysicalPoint().getPositionX());
			current_bucket_y = whichBucketY(a.getPhysicalPoint().getPositionY());
			
			// update the atom's position
			a.getPhysicalPoint().setPositionX((float)(a.getPhysicalPoint().getPositionX() + WORLD_SCALE*R * (a.getPhysicalPoint().getSpeedX() * dt + a.getPhysicalPoint().getAccelerationX() * halfdt2))); // halfdt2 = 0.5*dt*dt
			a.getPhysicalPoint().setPositionY((float)(a.getPhysicalPoint().getPositionY() + WORLD_SCALE*R * (a.getPhysicalPoint().getSpeedY() * dt + a.getPhysicalPoint().getAccelerationY() * halfdt2)));
			
			int new_bucket_x,new_bucket_y;
			new_bucket_x = whichBucketX(a.getPhysicalPoint().getPositionX());
			new_bucket_y = whichBucketY(a.getPhysicalPoint().getPositionY());
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

			a.getPhysicalPoint().setSpeedX((float)(a.getPhysicalPoint().getSpeedX() +  a.getPhysicalPoint().getAccelerationX() * halfdt)); // add old acceleration , halfdt = 0.5*dt
			a.getPhysicalPoint().setSpeedY((float)(a.getPhysicalPoint().getSpeedY() +  a.getPhysicalPoint().getAccelerationY() * halfdt));
		}
		computeAcceleration(width, height,is_dragging,which_being_dragged,mouse_x,mouse_y);
		for (int i = 0; i<atoms.length;i++)  // add new acceleration
		{
			Atom a = atoms[i];
			a.getPhysicalPoint().setSpeedX((float)(a.getPhysicalPoint().getSpeedX() + a.getPhysicalPoint().getAccelerationX() * halfdt));
			a.getPhysicalPoint().setSpeedY((float)( a.getPhysicalPoint().getSpeedY() + a.getPhysicalPoint().getAccelerationY() * halfdt));
		}
		// some possible alternative physics:
		// - a hard-sphere type physics, where instead of a constant timestep we search for future
		//   collisions between the spheres and run the sim forward to that point, and recompute their 
		//   velocities as a result of the collision. might be promising to explore for OB - but how to include
		//   bonds (and dragging)?
		// - a lattice-based physics can run very fast indeed but doesn't look as satisfying
	}

	// doesn't yet allow reactions, or bonds. Has severe problems with stability.
	private synchronized void computeAcceleration(int width, int height ,boolean is_dragging,int which_being_dragged,
		int mouse_x,int mouse_y)
	{
		float R = Atom.getAtomSize();
		// set the accelerations for this iteration to zero
		for(int i=0;i<atoms.length;i++) 
		{
			atoms[i].getPhysicalPoint().setAccelerationX(0);
			atoms[i].getPhysicalPoint().setAccelerationY(0);
		}
		for(int i=0;i<atoms.length-1;i++)
		{
			Atom a = atoms[i];
			// what square radius must we search for neighbours?
			double radius = 2.5*R;
			int rx = (int)Math.ceil(radius/bucket_width);
			int ry = (int)Math.ceil(radius/bucket_height);
			// what bucket is the atom in?
			int wx = whichBucketX(a.getPhysicalPoint().getPositionX());
			int wy = whichBucketY(a.getPhysicalPoint().getPositionY());
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
						double dx = a.getPhysicalPoint().getPositionX()-b.getPhysicalPoint().getPositionX();
						double dy = a.getPhysicalPoint().getPositionY()-b.getPhysicalPoint().getPositionY();
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
							a.getPhysicalPoint().setAccelerationX((float)(a.getPhysicalPoint().getAccelerationX() + fx));
							a.getPhysicalPoint().setAccelerationX((float)(a.getPhysicalPoint().getAccelerationY() + fy));
							b.getPhysicalPoint().setAccelerationX((float)(b.getPhysicalPoint().getAccelerationX() - fx));
							b.getPhysicalPoint().setAccelerationX((float)(b.getPhysicalPoint().getAccelerationY() - fy));
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
					case 0: dx=a.getPhysicalPoint().getPositionX(); break;
					case 1: dx=a.getPhysicalPoint().getPositionX()-width; break;
					case 2: dy=a.getPhysicalPoint().getPositionY(); break;
					case 3: dy=a.getPhysicalPoint().getPositionY()-height; break;
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
					a.getPhysicalPoint().setAccelerationX((float)(a.getPhysicalPoint().getAccelerationX() + fx));
					a.getPhysicalPoint().setAccelerationY((float)(a.getPhysicalPoint().getAccelerationY() + fy));
				}
			}
		}
		// the user can pull atoms about using the mouse (causes a problem)
		if(is_dragging)
		{
			Atom a = atoms[which_being_dragged];
			// normalise the pull vector
			float pullX = mouse_x-a.getPhysicalPoint().getPositionX();
			float pullY = mouse_y-a.getPhysicalPoint().getPositionY();
			float dist = (float)Math.sqrt(pullX*pullX+pullY*pullY);
			pullX /= dist;
			pullY /= dist;
			a.getPhysicalPoint().setAccelerationX((float)(a.getPhysicalPoint().getAccelerationX() + 3.0*R*pullX));
			a.getPhysicalPoint().setAccelerationY((float)(a.getPhysicalPoint().getAccelerationY() + 3.0*R*pullY));
		}
	}
}