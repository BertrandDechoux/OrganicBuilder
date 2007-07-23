package uk.org.squirm3.engine;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.DraggingPoint;
import uk.org.squirm3.data.Level;
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

//TODO synchronized ?
public class LocalEngine implements IApplicationEngine {
	
	private final EngineDispatcher engineDispatcher;
	
	private final Collider collider;
	private Thread thread;
	// simulation's attributes
	private int simulationHeight;
	private int simulationWidth;
	public boolean resetNeeded;
	private short sleepPeriod; // how many milliseconds to sleep for each iteration (user changeable)
	// things to do with the dragging around of atoms
	private DraggingPoint draggingPoint;
	private DraggingPoint lastUsedDraggingPoint;
	// level
	private Level currentLevel;
	private final List levelList;
	private int levelIndex;
	
	public LocalEngine(){	//TODO values should'nt be hardcoded, properties files ?
		levelList = new ArrayList();
		addLevels();
		engineDispatcher = new EngineDispatcher();
		simulationHeight = 500;
		simulationWidth = 500;
		resetNeeded = false;
		sleepPeriod = 50;
		collider = new Collider(50, simulationWidth, simulationHeight);
		goToLevel(0);
	}
	
	private void addLevels() {
		levelList.add(new Intro());
		levelList.add(new Join_As());
		levelList.add(new Make_ECs());
		levelList.add(new Line_Cs());
		levelList.add(new Join_all());
		levelList.add(new Connect_corners());
		levelList.add(new Abcdef_chains());
		levelList.add(new Join_same());
		levelList.add(new Match_template());
		levelList.add(new Break_molecule());
		levelList.add(new Bond_prisoner());
		levelList.add(new Pass_message());
		levelList.add(new Split_ladder());
		levelList.add(new Insert_atom());
		levelList.add(new Make_ladder());
		levelList.add(new Selfrep());
		levelList.add(new Grow_membrane());
		levelList.add(new Membrane_transport());
		levelList.add(new Membrane_division());
		levelList.add(new Cell_division());
		Iterator it = levelList.iterator();
		byte id = 0;
		while(it.hasNext()) {
			((Level)it.next()).setId(id++);
		}
	}

	public void clearReactions() {
		pauseSimulation();
		Vector reactions = collider.getReactions();
		reactions.clear();
		engineDispatcher.reactionsHaveChanged();
		runSimulation();
	}

	public Collection getAtoms() {
		Atom[] atoms = collider.getAtoms();
		List list = new LinkedList();
		for(int i = 0 ; i < atoms.length ; i++) {
			list.add(atoms[i]);
		}
		return list;
	}

	public short getAtomsNumber() {
		return (short)collider.getNumAtoms();
	}

	public DraggingPoint getCurrentDraggingPoint() {
		return draggingPoint;
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public DraggingPoint getLastUsedDraggingPoint() {
		return lastUsedDraggingPoint;
	}

	public Collection getReactions() {
		Vector reactions = collider.getReactions();
		List list = new LinkedList();
		list.addAll(reactions);
		return list;
	}

	public int getSimulationHeight() {
		return simulationHeight;
	}

	public short getSimulationSpeed() {
		return sleepPeriod;
	}

	public int getSimulationWidth() {
		return simulationWidth;
	}

	public void pauseSimulation() {
		Thread target = thread;
		thread = null;
		if(target!=null) {
			try {
				target.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		engineDispatcher.simulationStateHasChanged();
	}
	
	public void addReactions(Collection reactions) {
		pauseSimulation();
		Vector colliderReactions = collider.getReactions();
		colliderReactions.addAll(reactions);
		engineDispatcher.reactionsHaveChanged();
		runSimulation();	
	}

	public void removeReactions(Collection reactions) {
		pauseSimulation();
		Vector colliderReactions = collider.getReactions();
		colliderReactions.removeAll(reactions);
		engineDispatcher.reactionsHaveChanged();
		runSimulation();
	}

	public void restartLevel() {
		pauseSimulation();
	  	int nAtoms = collider.getNumAtoms();
		Atom[] newAtoms = currentLevel.resetAtoms(nAtoms, simulationWidth, simulationHeight);
		collider.setAtoms(newAtoms, simulationWidth, simulationHeight);
		engineDispatcher.atomsHaveChanged();
		needToRestartLevel(false);
	}
	
	private void needToRestartLevel(boolean b) {
		resetNeeded = b;
		engineDispatcher.simulationStateHasChanged();
	}

	public void runSimulation() {
		if(thread!=null || resetNeeded) return; // security check to avoid starting if already started
		thread = new Thread(
			new Runnable(){
				public void run()  {
					while (thread == Thread.currentThread()) {
						lastUsedDraggingPoint = draggingPoint;
						collider.doTimeStep(simulationWidth, simulationHeight, draggingPoint);
						engineDispatcher.atomsHaveChanged();
						try {
							Thread.sleep(sleepPeriod);
						} catch (InterruptedException e) { break; }
					}
				}
			});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		engineDispatcher.simulationStateHasChanged();
	}
	
	

	public void setAtomsNumber(short newAtomsNumber) {
		needToRestartLevel(true);
		pauseSimulation();
		collider.setNAtoms(newAtomsNumber);
		engineDispatcher.atomsNumberHasChanged();
	}

	public void setDraggingPoint(DraggingPoint newDraggingPoint) {
		if(draggingPoint==null && newDraggingPoint==null) return;
		if(draggingPoint==null && newDraggingPoint!=null
				|| draggingPoint!=null && newDraggingPoint==null) {
			draggingPoint = newDraggingPoint;
			engineDispatcher.draggingPointHasChanged();
			return;
		}
		if(draggingPoint.equals(newDraggingPoint)) return;
		draggingPoint = newDraggingPoint;
		engineDispatcher.draggingPointHasChanged();
	}

	public void setReactions(Collection reactions) {
		pauseSimulation();
		Vector colliderReactions = collider.getReactions();
		colliderReactions.clear();
		colliderReactions.addAll(reactions);
		engineDispatcher.reactionsHaveChanged();
		runSimulation();
	}

	public void setSimulationSize(int width, int height) {
		simulationWidth = width;
		simulationHeight = height;
		engineDispatcher.simulationSizeHasChanged();
	}

	public void setSimulationSpeed(short newSleepPeriod) {
		sleepPeriod = newSleepPeriod;
		engineDispatcher.simulationSpeedHasChanged();
	}

	public boolean simulationIsRunning() {
		return thread!=null;
	}

	public boolean simulationNeedReset() {
		return resetNeeded;
	}

	public void addEngineListener(IEngineListener listener) {
		engineDispatcher.addEngineListener(listener);
	}

	public void removeEngineListener(IEngineListener listener) {
		engineDispatcher.removeEngineListener(listener);
	}

	public void addAtomListener(IAtomListener listener) {
		engineDispatcher.addAtomListener(listener);
	}

	public void removeAtomListener(IAtomListener listener) {
		engineDispatcher.removeAtomListener(listener);
	}

	public void addLevelListener(ILevelListener listener) {
		engineDispatcher.addLevelListener(listener);
	}

	public void removeLevelListener(ILevelListener listener) {
		engineDispatcher.removeLevelListener(listener);
	}

	public void addPropertyListener(IPropertyListener listener) {
		engineDispatcher.addPropertyListener(listener);
	}

	public void removePropertyListener(IPropertyListener listener) {
		engineDispatcher.removePropertyListener(listener);
	}

	public void addReactionListener(IReactionListener listener) {
		engineDispatcher.addReactionListener(listener);
	}

	public void removeReactionListener(IReactionListener listener) {
		engineDispatcher.removeReactionListener(listener);
	}

	public void addStateListener(IStateListener listener) {
		engineDispatcher.addStateListener(listener);
	}

	public void removeStateListener(IStateListener listener) {
		engineDispatcher.removeStateListener(listener);
	}

	public void goToLevel(int levelIndex) {
		this.levelIndex = levelIndex;
		currentLevel = (Level)levelList.get(levelIndex);
		pauseSimulation();
		collider.setReactions(new Reaction[0]);
		engineDispatcher.reactionsHaveChanged();
	  	int nAtoms = collider.getNumAtoms();
		Atom[] newAtoms = currentLevel.resetAtoms(nAtoms, simulationWidth, simulationHeight);
		collider.setAtoms(newAtoms, simulationWidth, simulationHeight);
		engineDispatcher.atomsHaveChanged();
		engineDispatcher.levelHasChanged();
		runSimulation();
	}

	public void goToFirstLevel() {
		goToLevel(0);
	}

	public void goToLastLevel() {
		goToLevel(levelList.size()-1);
	}

	public void goToNextLevel() {
		if(levelIndex+1<levelList.size())
			goToLevel(levelIndex+1);
	}

	public void goToPreviousLevel() {
		if(levelIndex-1>=0)
			goToLevel(levelIndex-1);
	}

	public List getLevels() {
		return levelList;
	}

}

//*******************************************************************

class Intro extends Level //0
{

	public Intro() {
		super(Application.localize(new String[] {"levels","intro","title" }),
				Application.localize(new String[] {"levels","intro","challenge" }),
				Application.localize(new String[] {"levels","intro","title" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		return Level.getDefaultReset(numberOfAtoms, width, height);
	}
	
	public String evaluate(Atom[] atoms) { return null; } // Is this one called even one time ???
}


//*******************************************************************

class Join_As extends Level //1
{

	public Join_As() {
		super(Application.localize(new String[] {"levels","joinas","title" }),
				Application.localize(new String[] {"levels","joinas","challenge" }),
				Application.localize(new String[] {"levels","joinas","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
		// is any non-'a' atom bonded with any other?
		for(int i=0;i<atoms.length;i++)
			if(atoms[i].type!=0 && atoms[i].bonds.size()>0)
				return Application.localize(new String[] {"levels","joinas","error","1" });
		// is every 'a' atom bonded together in a big clump?
		LinkedList a_atoms = new LinkedList();
		for(int i=0;i<atoms.length;i++) {
			if(atoms[i].type==0) {
				// this will do as our starting point
				atoms[i].getAllConnectedAtoms(a_atoms);
				break;
			}
		}
		for(int i=0;i<atoms.length;i++) 
			if(atoms[i].type==0 && !a_atoms.contains(atoms[i]))
				return Application.localize(new String[] {"levels","joinas","error","2" });
		return null;
	}
}

//*******************************************************************

class Make_ECs extends Level //2
{

	public Make_ECs() {
		super(Application.localize(new String[] {"levels","makeecs","title" }),
				Application.localize(new String[] {"levels","makeecs","challenge" }),
				Application.localize(new String[] {"levels","makeecs","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				// each atom must be either 'e' and bonded to just a 'c' (or vice versa), or unbonded
			int ec_pairs_found=0,loose_e_atoms_found=0,loose_c_atoms_found=0;
			for(int i=0;i<atoms.length;i++) {
				Atom atom = atoms[i];
				if(atom.type!=2 && atom.type!=4 && atom.bonds.size()!=0)
					return Application.localize(new String[] {"levels","makeecs","error","1" });
				if(atom.type==2 || atom.type==4) {
					if(atom.bonds.size()>1)
						return Application.localize(new String[] {"levels","makeecs","error","2" });
					if(atom.bonds.size()==0) {
						if(atom.type==2) loose_c_atoms_found++;
						else loose_e_atoms_found++;
					}
				}
			}
			if(Math.min(loose_c_atoms_found,loose_e_atoms_found)>0)
				return Application.localize(new String[] {"levels","makeecs","error","3" });
	return null; }
}

//*******************************************************************

class Line_Cs extends Level //3
{

	public Line_Cs() {
		super(Application.localize(new String[] {"levels","linecs","title" }),
				Application.localize(new String[] {"levels","linecs","challenge" }),
				Application.localize(new String[] {"levels","linecs","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		newAtoms[0].state=1; 
		newAtoms[0].type=2; 
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				int single_bonded_atoms_found=0,double_bonded_atoms_found=0;
			// get the set of atoms joined to atom[0]
			LinkedList joined = new LinkedList();
			atoms[0].getAllConnectedAtoms(joined);
			// fail on bonds<1 or >2, or not in 'joined' list
			for(int i=0;i<atoms.length;i++) {
				Atom atom = atoms[i];
				if(atom.type!=2) {
					if(atom.bonds.size()!=0) 
						return Application.localize(new String[] {"levels","linecs","error","1" });;
					continue; // no other tests for non-'c' atoms
				}
				if(atom.bonds.size()==1) single_bonded_atoms_found++;
				else if(atom.bonds.size()==2) double_bonded_atoms_found++;
				else if(atom.bonds.size()==0)
					return Application.localize(new String[] {"levels","linecs","error","2" });
				else
					return Application.localize(new String[] {"levels","linecs","error","3" });
				if(!joined.contains(atom))
					return Application.localize(new String[] {"levels","linecs","error","4" });
			}
			// one final check on chain configuration
			if(single_bonded_atoms_found!=2)
				return Application.localize(new String[] {"levels","linecs","error","5" });
	 return null; }
}

//*******************************************************************

class Join_all extends Level //4
{

	public Join_all() {
		super(Application.localize(new String[] {"levels","joinall","title" }),
				Application.localize(new String[] {"levels","joinall","challenge" }),
				Application.localize(new String[] {"levels","joinall","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				// all joined?
			LinkedList joined = new LinkedList();
			atoms[0].getAllConnectedAtoms(joined);
			if(joined.size()!=atoms.length)
				return Application.localize(new String[] {"levels","joinall","error","1" });
	return null; }
}

//*******************************************************************

class Connect_corners extends Level //5
{

	public Connect_corners() {
		super(Application.localize(new String[] {"levels","connectcorners","title" }),
				Application.localize(new String[] {"levels","connectcorners","challenge" }),
				Application.localize(new String[] {"levels","connectcorners","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		newAtoms[0].state=1; 
		newAtoms[0].type=5; 
		newAtoms[0].pos.x=newAtoms[0].pos.y=atomSize*1.5f; 
		newAtoms[0].stuck=true; 
		newAtoms[1].state=1; 
		newAtoms[1].type=3; 
		newAtoms[1].pos.x=width-atomSize*1.5f;
		newAtoms[1].pos.y=height-atomSize*1.5f;
		newAtoms[1].stuck=true; 
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) { 
				// 0 joined to 1?
			LinkedList joined = new LinkedList();
			atoms[0].getAllConnectedAtoms(joined);
			if(!joined.contains(atoms[1]))
				return Application.localize(new String[] {"levels","connectcorners","error","1" });
	return null; }
}

//*******************************************************************

class Abcdef_chains extends Level //6
{

	public Abcdef_chains() {
		super(Application.localize(new String[] {"levels","abcdefchains","title" }),
				Application.localize(new String[] {"levels","abcdefchains","challenge" }),
				Application.localize(new String[] {"levels","abcdefchains","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				// how many abcdef chains are there?
			int num_abcdef_chains_found=0;
			for(int i=0;i<atoms.length;i++) {
				Atom a = atoms[i];
				if(a.type==0 && a.bonds.size()==1) {
					// looks promising - let's check
					LinkedList joined = new LinkedList();
					a.getAllConnectedAtoms(joined);
					if(joined.size()!=6) continue;
					Iterator it = joined.iterator();
					if(((Atom)it.next()).type==0 && ((Atom)it.next()).type==1
						&& ((Atom)it.next()).type==2 && ((Atom)it.next()).type==3
						&& ((Atom)it.next()).type==4 && ((Atom)it.next()).type==5)
						num_abcdef_chains_found++;
					// (this isn't a perfect test but hopefully close enough)
				}
			}
			if(num_abcdef_chains_found==0)
				return Application.localize(new String[] {"levels","abcdefchains","error","1" });
			else if(num_abcdef_chains_found==1)
				return Application.localize(new String[] {"levels","abcdefchains","error","1" });
	return null; }
}

//*******************************************************************

class Join_same extends Level //7
{

	public Join_same() {
		super(Application.localize(new String[] {"levels","joinsame","title" }),
				Application.localize(new String[] {"levels","joinsame","challenge" }),
				Application.localize(new String[] {"levels","joinsame","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				for(int i=0;i<atoms.length;i++) {	
				// get everything that's joined to this atom
				LinkedList joined = new LinkedList();
				Atom atom = atoms[i];
				atom.getAllConnectedAtoms(joined);
				// is there any atom in this list of a different type?
				Iterator it = joined.iterator();
				while(it.hasNext()) {
					Atom other = (Atom)it.next();
					if(other.type != atom.type)
						return Application.localize(new String[] {"levels","joinsame","error","1" });
				}
				// are there any atoms of the same type not on this list?
				for(int j=0;j<atoms.length;j++)
					if(atoms[j].type == atom.type && !joined.contains(atoms[j]))
						return Application.localize(new String[] {"levels","joinsame","error","1" });
			}
	return null; }
}

//*******************************************************************

class Match_template extends Level //8
{

	public Match_template() {
		super(Application.localize(new String[] {"levels","matchtemplate","title" }),
				Application.localize(new String[] {"levels","matchtemplate","challenge" }),
				Application.localize(new String[] {"levels","matchtemplate","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
				// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*4.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond six random atoms to form a template
		for(int i=0;i<6;i++)
		{
			newAtoms[i].pos.x = atomSize*1.5f;
			newAtoms[i].pos.y = atomSize*5.0f+i*atomSize*2.1f;
			newAtoms[i].state=1;
			newAtoms[i].type=PRNG.nextInt(6);
			if(i>0)
				newAtoms[i].bondWith(newAtoms[i-1]);
			newAtoms[i].stuck=true;
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				// does each atom 0-5 have another single type-matching atom attached?
			for(int i=0;i<6;i++) {
				Atom a = atoms[i];
				Atom b = (Atom)a.bonds.getLast();
				if(b.type!=a.type || b.bonds.size()!=1)
					return Application.localize(new String[] {"levels","matchtemplate","error","1" });
				// (not a complete test, but hopefully ok)
			}
	 return null; }
}

//*******************************************************************

class Break_molecule extends Level //9
{

	public Break_molecule() {
		super(Application.localize(new String[] {"levels","breakmolecule","title" }),
				Application.localize(new String[] {"levels","breakmolecule","challenge" }),
				Application.localize(new String[] {"levels","breakmolecule","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
			// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*4.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond n atoms to form a template
		for(int i=0;i<10;i++)
		{
			newAtoms[i].pos.x = atomSize*1.5f;
			newAtoms[i].pos.y = atomSize*1.5f+i*atomSize*2.1f;
			newAtoms[i].state=1;
			if(i<5)
				newAtoms[i].type=0; // 'a'
			else 
				newAtoms[i].type=3; // 'd'
			if(i>0)
				newAtoms[i].bondWith(newAtoms[i-1]);
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				int n_bonds[]={1,2,2,2,1,1,2,2,2,1};
			for(int i=0;i<10;i++)
				if(atoms[i].bonds.size() != n_bonds[i])
					return Application.localize(new String[] {"levels","breakmolecule","error","1" });
	return null; }
}

//*******************************************************************

class Bond_prisoner extends Level //10
{

	public Bond_prisoner() {
		super(Application.localize(new String[] {"levels","bondprisoner","title" }),
				Application.localize(new String[] {"levels","bondprisoner","challenge" }),
				Application.localize(new String[] {"levels","bondprisoner","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*8.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
			newAtoms[i].type = (i%5)+1; // free atoms in states b-f only (for grow_membrane level)
		}
		// place and bond 8 atoms to form a loop
		int pos_x[]={-1,0,1,1,1,0,-1,-1};
		int pos_y[]={-1,-1,-1,0,1,1,1,0};
		for(int i=0;i<8;i++)
		{
			newAtoms[i].pos.x = atomSize*4.0f + pos_x[i]*atomSize*2.0f;
			newAtoms[i].pos.y = atomSize*7.0f + pos_y[i]*atomSize*2.0f;
			if(i==0)
				newAtoms[i].state=3;
			else if(i==1)
				newAtoms[i].state=4;
			else
				newAtoms[i].state=2;
			newAtoms[i].type=0;
			newAtoms[i].bondWith(newAtoms[(i+1)%8]);
		}
		// add the prisoner (f1)
		newAtoms[8].pos.x = atomSize*4.0f;
		newAtoms[8].pos.y = atomSize*7.0f;
		newAtoms[8].state=1;
		newAtoms[8].type=5;
		// make sure that there's at least one f elsewhere
		newAtoms[9].pos.x = atomSize*10.0f;
		newAtoms[9].pos.y = atomSize*7.0f;
		newAtoms[9].state=0;
		newAtoms[9].type=5;
		// make sure there are exactly 6 free a atoms elsewhere to grow the membrane with (also ok for bond_prisoner level)
		for(int i=10;i<10+6;i++)
			newAtoms[i].type=0;
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				// if atom 8 bonded with an f?
			if(atoms[8].bonds.size()==0)
				return Application.localize(new String[] {"levels","bondprisonner","error","1" });
			if(((Atom)atoms[8].bonds.getFirst()).type!=5)
				return Application.localize(new String[] {"levels","bondprisonner","error","2" });
	return null; }
}

//*******************************************************************

class Pass_message extends Level //11
{

	public Pass_message() {
		super(Application.localize(new String[] {"levels","passmessage","title" }),
				Application.localize(new String[] {"levels","passmessage","challenge" }),
				Application.localize(new String[] {"levels","passmessage","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
			// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*4.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond n atoms to form a template
		for(int i=0;i<10;i++)
		{
			newAtoms[i].pos.x = atomSize*1.5f;
			newAtoms[i].pos.y = atomSize*1.5f+i*atomSize*2.1f;
			newAtoms[i].type=PRNG.nextInt(6);
			if(i==0)
				newAtoms[i].state=2;
			else 
				newAtoms[i].state=1;
			if(i>0)
				newAtoms[i].bondWith(newAtoms[i-1]);
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				int n_bonds[]={1,2,2,2,2,2,2,2,2,1};
			for(int i=0;i<10;i++)
				if(atoms[i].bonds.size() != n_bonds[i])
					return Application.localize(new String[] {"levels","passmessage","error","1" });
				else if(atoms[i].state != 2)
					return Application.localize(new String[] {"levels","passmessage","error","2" });
	return null; }
}

//*******************************************************************

class Split_ladder extends Level //12
{

	public Split_ladder() {
		super(Application.localize(new String[] {"levels","splitladder","title" }),
				Application.localize(new String[] {"levels","splitladder","challenge" }),
				Application.localize(new String[] {"levels","splitladder","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
				// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*6.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond n atoms to form a template
		for(int i=0;i<20;i++)
		{
			newAtoms[i].pos.x = (i<10)?atomSize*1.5f:atomSize*3.7f;
			newAtoms[i].pos.y = (i<10)?(atomSize*6.0f+i*atomSize*2.1f):(atomSize*6.0f+(i-10)*atomSize*2.1f);
			newAtoms[i].type=(i<10)?(PRNG.nextInt(6)):newAtoms[i-10].type;
			if(i==0 || i==10)
				newAtoms[i].state=2;
			else
				newAtoms[i].state=1;
			if(i!=0 && i!=10)
				newAtoms[i].bondWith(newAtoms[i-1]);
			if(i>=10)
				newAtoms[i].bondWith(newAtoms[i-10]);
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				int n_bonds[]={1,2,2,2,2,2,2,2,2,1};
			for(int i=0;i<20;i++)
				if(atoms[i].bonds.size() != n_bonds[i%10])
					return Application.localize(new String[] {"levels","splitladder","error","1" });
	return null; }
}

//*******************************************************************

class Insert_atom extends Level //13
{

	public Insert_atom() {
		super(Application.localize(new String[] {"levels","insertatom","title" }),
				Application.localize(new String[] {"levels","insertatom","challenge" }),
				Application.localize(new String[] {"levels","insertatom","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
				// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*4.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond n atoms to form a template
		for(int i=0;i<10;i++)
		{
			newAtoms[i].pos.x = atomSize*1.5f;
			newAtoms[i].pos.y = atomSize*3.0f+i*atomSize*2.1f;
			newAtoms[i].type=4; // 'e'
			if(i==4)
				newAtoms[i].state=2; 
			else if(i==5)
				newAtoms[i].state=3; 
			else newAtoms[i].state=1;
			if(i>0)
				newAtoms[i].bondWith(newAtoms[i-1]);
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				LinkedList joined = new LinkedList();
			atoms[0].getAllConnectedAtoms(joined);
			if(joined.size()!=11)
				return Application.localize(new String[] {"levels","insertatom","error","1" });
			int n_bonds[]={1,2,2,2,2,2,2,2,2,2,1};
			int types[]={4,4,4,4,4,1,4,4,4,4,4};
			int i = 0;
			Iterator it = joined.iterator();
			while(it.hasNext()) {
				Atom a = (Atom)it.next();
				if(a.bonds.size()!=n_bonds[i])
					return Application.localize(new String[] {"levels","insertatom","error","2" });
				if( a.type!=types[i])
					return Application.localize(new String[] {"levels","insertatom","error","3" });
				i++;
			}
	return null; }
}

//*******************************************************************

class Make_ladder extends Level //14
{

	public Make_ladder() {
		super(Application.localize(new String[] {"levels","makeladder","title" }),
				Application.localize(new String[] {"levels","makeladder","challenge" }),
				Application.localize(new String[] {"levels","makeladder","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
				// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*4.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond six atoms to form a template
		for(int i=0;i<6;i++)
		{
			newAtoms[i].pos.x = atomSize*1.5f;
			newAtoms[i].pos.y = atomSize*6.0f+i*atomSize*2.1f;
			newAtoms[i].state=1;
			if(i==0)
				newAtoms[i].type=4; // 'e' at the top
			else if(i==5)
				newAtoms[i].type=5; // 'f' at the bottom
			else
				newAtoms[i].type=PRNG.nextInt(4); // 'a'-'d'
			if(i>0)
					newAtoms[i].bondWith(newAtoms[i-1]);
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				LinkedList joined = new LinkedList();
			atoms[0].getAllConnectedAtoms(joined);
			if(joined.size()>12)
				return Application.localize(new String[] {"levels","makeladder","error","1" });
			else if(joined.size()<12)
				return Application.localize(new String[] {"levels","makeladder","error","2" });
			// are the types matching?
			int original_type_count[] = {0,0,0,0,0,0},new_type_count[]={0,0,0,0,0,0};
			for(int i=0;i<6;i++) original_type_count[atoms[i].type]++;
			Iterator it = joined.iterator();
			while(it.hasNext()) new_type_count[((Atom)it.next()).type]++;
			for(int i=0;i<6;i++)
				if(new_type_count[i] != original_type_count[i]*2) 
					return Application.localize(new String[] {"levels","makeladder","error","3" });
			it = joined.iterator();
			while(it.hasNext()) { 
				Atom a = (Atom)it.next();
				if(a.type==4 || a.type==5) // 'e' and 'f' 
					if(a.bonds.size()!=2) 
						return Application.localize(new String[] {"levels","makeladder","error","4" });
				else if(a.bonds.size()!=3)  
					return Application.localize(new String[] {"levels","makeladder","error","5" });
			}
	return null; }
}

//*******************************************************************

class Selfrep extends Level //15
{

	public Selfrep() {
		super(Application.localize(new String[] {"levels","selfrep","title" }),
				Application.localize(new String[] {"levels","selfrep","challenge" }),
				Application.localize(new String[] {"levels","selfrep","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
		// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*4.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond six atoms to form a template
		
		// (ensure each type is used once (to allow multiple copies to be made))
		int[][] genomes = {{0,1,3,2},{3,1,2,0},{2,3,0,1},{1,2,0,3},{0,3,2,1}};
		int which_genome = PRNG.nextInt(5); 
		for(int i=0;i<6;i++)
		{
			newAtoms[i].pos.x = atomSize*1.5f;
			newAtoms[i].pos.y = atomSize*6.0f+i*atomSize*2.1f;
			newAtoms[i].state=1;
			if(i==0)
				newAtoms[i].type=4; // 'e' at the top
			else if(i==5)
				newAtoms[i].type=5; // 'f' at the bottom
			else
				newAtoms[i].type=genomes[which_genome][i-1];
			if(i>0)
				newAtoms[i].bondWith(newAtoms[i-1]);
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) { // improved code from Ralph Hartley
	LinkedList joined = new LinkedList();
			// there should be at least two bonded 'e' atoms in the world, each at the head of a copy
			int n_found = 0;
			int bound_atoms = 0;
			for(int i=0;i<atoms.length;i++) { // include the original
				Atom first = atoms[i];
				if(first.bonds.size()>0) {
					bound_atoms++;
					
					if(first.type==4) {
						joined.clear();
						first.getAllConnectedAtoms(joined);
						
						if(joined.size()!=6)
							return Application.localize(new String[] {"levels","selfrep","error","1" });
						
						Atom last = (Atom)joined.getLast();
						if (first.bonds.size()!=1 || last.bonds.size()!=1 || last.type!=5)
							return Application.localize(new String[] {"levels","selfrep","error","2" });
						
						for(int j=1;j<joined.size()-1;j++) {
							Atom a = (Atom)joined.get(j);
							if (a.bonds.size()!=2)
								return Application.localize(new String[] {"levels","selfrep","error","3" });				
							if(a.type != atoms[j].type)
								return Application.localize(new String[] {"levels","selfrep","error","4" });
						}
						n_found++;
					}
				}
			}
			
			if(n_found==0)
				return Application.localize(new String[] {"levels","selfrep","error","5" });
			if(n_found<2)
				return Application.localize(new String[] {"levels","selfrep","error","6" });
			if (bound_atoms!=6*n_found)
				return Application.localize(new String[] {"levels","selfrep","error","7" });
	return null; }
}

//*******************************************************************

class Grow_membrane extends Level //16
{

	public Grow_membrane() {
		super(Application.localize(new String[] {"levels","growmembrane","title" }),
				Application.localize(new String[] {"levels","growmembrane","challenge" }),
				Application.localize(new String[] {"levels","growmembrane","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*8.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
			newAtoms[i].type = (i%5)+1; // free atoms in states b-f only (for grow_membrane level)
		}
		// place and bond 8 atoms to form a loop
		int pos_x[]={-1,0,1,1,1,0,-1,-1};
		int pos_y[]={-1,-1,-1,0,1,1,1,0};
		for(int i=0;i<8;i++)
		{
			newAtoms[i].pos.x = atomSize*4.0f + pos_x[i]*atomSize*2.0f;
			newAtoms[i].pos.y = atomSize*7.0f + pos_y[i]*atomSize*2.0f;
			if(i==0)
				newAtoms[i].state=3;
			else if(i==1)
				newAtoms[i].state=4;
			else
				newAtoms[i].state=2;
			newAtoms[i].type=0;
			newAtoms[i].bondWith(newAtoms[(i+1)%8]);
		}
		// add the prisoner (f1)
		newAtoms[8].pos.x = atomSize*4.0f;
		newAtoms[8].pos.y = atomSize*7.0f;
		newAtoms[8].state=1;
		newAtoms[8].type=5;
		// make sure that there's at least one f elsewhere
		newAtoms[9].pos.x = atomSize*10.0f;
		newAtoms[9].pos.y = atomSize*7.0f;
		newAtoms[9].state=0;
		newAtoms[9].type=5;
		// make sure there are exactly 6 free a atoms elsewhere to grow the membrane with (also ok for bond_prisoner level)
		for(int i=10;i<10+6;i++)
			newAtoms[i].type=0;
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
	// starting from atom 0 there should be a neat closed loop of a atoms
			LinkedList joined = new LinkedList();
			atoms[0].getAllConnectedAtoms(joined);
			// (each atom in the connected group should of type 'a' and have exactly two bonds (hence a neat loop))
			int x_points[] = new int[joined.size()],y_points[]=new int[joined.size()];
			Iterator it = joined.iterator();
			int i = 0;
			while(it.hasNext()) {
				Atom a = (Atom)it.next();
				if(a.type!=0 || a.bonds.size()!=2)
					return Application.localize(new String[] {"levels","growmembrane","error","1" });
				x_points[i] = (int)a.pos.x; // (need these for polygon check, below)
				y_points[i] = (int)a.pos.y;
				i++;
			}
			// inside the polygon formed by the a atoms there should be exactly one atom - the original f1 (although its state may have changed)
			Atom f1 = atoms[8]; // see the setup code for this level
			Polygon poly = new Polygon(x_points,y_points,joined.size());
			if(!poly.contains(f1.pos))
				return Application.localize(new String[] {"levels","growmembrane","error","2" });
			// and no other 'a' atoms around
			for(i=0;i<atoms.length;i++) {
				Atom a = atoms[i];
				if(!joined.contains(a) && a.type==0)
					return Application.localize(new String[] {"levels","growmembrane","error","3" });
			}
	return null; }
}

//*******************************************************************

class Membrane_transport extends Level //17
{

	public Membrane_transport() {
		super(Application.localize(new String[] {"levels","membranetransport","title" }),
				Application.localize(new String[] {"levels","membranetransport","challenge" }),
				Application.localize(new String[] {"levels","membranetransport","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
				// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*8.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
			newAtoms[i].type = (i%5); // just types a-e in the wild, since we want to control the number of f's
		}
		// place and bond N atoms to form a loop
		final int N=12;
		int pos_y[]={-1,0,1,2,3,3,3,2,1,0,-1,-1}; // reading clockwise from the top-left corner (y is down)
		int pos_x[]={-1,-1,-1,-1,-1,0,1,1,1,1,1,0};
		int i; // atom index incremented in loops but also used elsewhere without resetting
		for(i=0;i<N;i++)
		{
			newAtoms[i].pos.x = atomSize*4.0f + pos_x[i]*atomSize*2.0f;
			newAtoms[i].pos.y = atomSize*7.0f + pos_y[i]*atomSize*2.0f;
			if(i==0)
				newAtoms[i].state=3;
			else if(i==1)
				newAtoms[i].state=4;
			else
				newAtoms[i].state=2;
			newAtoms[i].type=0;
			newAtoms[i].bondWith(newAtoms[(i+1)%N]);
		}
		// put two e1 atoms and one b1 atom inside
		int so_far=i;
		for(;i<so_far+3;i++)
		{
			newAtoms[i].pos.y = atomSize*(7.0f+(i-so_far)*2.0f);
			newAtoms[i].pos.x = atomSize*4.0f;
			newAtoms[i].state=1;
			newAtoms[i].type=((i-so_far)==0?1:4);
		}
		// make sure there are exactly 4 free f atoms elsewhere to put inside
		so_far=i;
		for(;i<so_far+4;i++)
			newAtoms[i].type=5;
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
				// starting from atom 0 there should be a neat closed loop of a atoms
			LinkedList joined = new LinkedList();
			atoms[0].getAllConnectedAtoms(joined);
			// (each atom in the connected group should of type 'a' and have exactly two bonds (hence a neat loop))
			int x_points[] = new int[joined.size()],y_points[]=new int[joined.size()];
			Iterator it = joined.iterator();
			int i = 0;
			while(it.hasNext()) {
				Atom a = (Atom)it.next();
				if(a.type!=0 || a.bonds.size()!=2)
					return Application.localize(new String[] {"levels","membranetransport","error","1" });
				x_points[i] = (int)a.pos.x; // (need these for polygon check, below)
				y_points[i] = (int)a.pos.y;
				i++;
			}
			// inside should be the original 'b' atom, and all the 'f' atoms, and nothing else
			Atom b1 = atoms[12]; // see the setup code for this level
			Polygon poly = new Polygon(x_points,y_points,joined.size());
			if(!poly.contains(b1.pos))
				return Application.localize(new String[] {"levels","membranetransport","error","2" });
			// check the other atoms (want: f's inside, other's outside)
			for(i=joined.size()+1;i<atoms.length;i++) {
				Atom a = atoms[i];
				if(a.type==5 && !poly.contains(a.pos))
					return Application.localize(new String[] {"levels","membranetransport","error","3" });
				else if(a.type!=5 && poly.contains(a.pos))
					return Application.localize(new String[] {"levels","membranetransport","error","4" });
			}
	return null; }
}

//*******************************************************************

class Membrane_division extends Level //18
{

	public Membrane_division() {
		super(Application.localize(new String[] {"levels","membranedivision","title" }),
				Application.localize(new String[] {"levels","membranedivision","challenge" }),
				Application.localize(new String[] {"levels","membranedivision","hint" }));
	}
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*8.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
		}
		// place and bond N atoms to form a loop
		final int N=12;
		int pos_y[]={-1,0,1,2,3,3,3,2,1,0,-1,-1}; // reading clockwise from the top-left corner (y is down)
		int pos_x[]={-1,-1,-1,-1,-1,0,1,1,1,1,1,0};
		for(int i=0;i<N;i++)
		{
			newAtoms[i].pos.x = atomSize*4.0f + pos_x[i]*atomSize*2.0f;
			newAtoms[i].pos.y = atomSize*7.0f + pos_y[i]*atomSize*2.0f;
			if(i==0)
				newAtoms[i].state=3;
			else if(i==N/2)
				newAtoms[i].state=4;
			else
				newAtoms[i].state=2;
			newAtoms[i].type=0;
			newAtoms[i].bondWith(newAtoms[(i+1)%N]);
		}
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
	
				final int N=12; // original loop size (see setup code, above)
			// starting from atom 0 there should be a neat closed loop of a atoms
			LinkedList loop[] = {new LinkedList(),new LinkedList()};
			atoms[0].getAllConnectedAtoms(loop[0]);
			if(loop[0].size()>=N)
				return Application.localize(new String[] {"levels","membranedivision","error","1" });
			// and there should be a second loop of 'a' atoms made of the same atoms
			for(int i=0;i<N;i++) {
				Atom a = atoms[i];
				if(!loop[0].contains(a)) a.getAllConnectedAtoms(loop[1]);
			}
			if(loop[0].size()+loop[1].size()!=N)
				return Application.localize(new String[] {"levels","membranedivision","error","2" });
			// each atom in each group should of type 'a' and have exactly two bonds (hence a neat loop)
			for(int iLoop=0;iLoop<2;iLoop++) {
				for(int i=0;i<loop[iLoop].size();i++) {
					Atom a = (Atom)loop[iLoop].get(i);
					if(a.type!=0 || a.bonds.size()!=2)
						return Application.localize(new String[] {"levels","membranedivision","error","3" });
				}
			}
			return null; }
}

//*******************************************************************

class Cell_division extends Level //19
{

	public Cell_division() {
		super(Application.localize(new String[] {"levels","celldivision","title" }),
				Application.localize(new String[] {"levels","celldivision","challenge" }),
				Application.localize(new String[] {"levels","celldivision","hint" }));
	}
	
	
	public Atom[] resetAtoms(int numberOfAtoms, int width, int height){
		Atom[] newAtoms = Level.getDefaultReset(numberOfAtoms, width, height);
		float atomSize = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
		// first move all the atoms out of the way
		for(int i=0;i<newAtoms.length;i++)
		{
			while(newAtoms[i].pos.x < atomSize*8.0f)
				newAtoms[i].pos.x += atomSize*2.0f;
			final int n_extra=6;
			newAtoms[i].type = Math.max(0,i%(6+n_extra)-n_extra); // extra a's for membrane growth
		}
		// place and bond N atoms to form a loop
		final int N=18;
		int pos_y[]={-1,0,1,2,3,4,5,6,6,6,5,4,3,2,1,0,-1,-1}; // reading clockwise from the top-left corner (y is down)
		int pos_x[]={-1,-1,-1,-1,-1,-1,-1,-1,0,1,1,1,1,1,1,1,1,0};
		int i; // atom index incremented in loops but also used elsewhere without resetting
		for(i=0;i<N;i++)
		{
			newAtoms[i].pos.x = atomSize*4.0f + pos_x[i]*atomSize*2.0f;
			newAtoms[i].pos.y = atomSize*7.0f + pos_y[i]*atomSize*2.0f;
			if(i==N-1 || i==N/2-1)
				newAtoms[i].state=3;
			else
				newAtoms[i].state=2;
			newAtoms[i].type=0;
			newAtoms[i].bondWith(newAtoms[(i+1)%N]);
		}
		// place and bond six atoms to form a template
		// (ensure each type is used once (to allow multiple copies to be made, if they want))
		int[][] genomes = {{0,1,3,2},{3,1,2,0},{2,3,0,1},{1,2,0,3},{0,3,2,1}};
		int which_genome = PRNG.nextInt(5); 
		int so_far=i;
		for(;i<so_far+6;i++)
		{
			newAtoms[i].pos.x = atomSize*4.0f;
			newAtoms[i].pos.y = atomSize*7.0f+(i-so_far)*atomSize*2.0f;
			newAtoms[i].state=1;
			if(i-so_far==0)
			{
				newAtoms[i].type=4; // 'e' at the top
				newAtoms[i].bondWith(newAtoms[N-1]);
			}
			else if(i-so_far==5)
			{
				newAtoms[i].type=5; // 'f' at the bottom
				newAtoms[i].bondWith(newAtoms[N/2-1]);
				newAtoms[i].bondWith(newAtoms[i-1]);
			}
			else
			{
				newAtoms[i].type=genomes[which_genome][(i-so_far)-1];
				newAtoms[i].bondWith(newAtoms[i-1]);
			}
		}
		// set one of the free-floating atoms to be a killer enzyme we must exclude from the cell
		newAtoms[i].killer = true;
		return newAtoms;
	}
	
	public String evaluate(Atom[] atoms) {
	
	// evaluation of this level is quite tricky. we could be strict and insist on neat membranes but that is
			// not really the intention.
			
			// pseudocode: non-embedded connected components, identification of the copies, check for template copy

			LinkedList components = new LinkedList(); // stores a list of LinkedList's, the components
			for(int i=0;i<atoms.length;i++) {
				Atom a = atoms[i];
				// is this atom already in a connected component?
				boolean already_seen=false;
				for(int iComponent=0;iComponent<components.size();iComponent++) {
					if(((LinkedList)components.get(iComponent)).contains(a)) {
						already_seen=true;
						break;
					}
				}
				if(already_seen) continue;
				// create a new connected component starting from this atom
				LinkedList component = new LinkedList();
				a.getAllConnectedAtoms(component);
				if(component.size()>6) // only interested in larger groups
					components.add(component);
			}

			if(components.size()!=2)  // lets enforce that there should be exactly two large components
				return Application.localize(new String[] {"levels","celldivision","error","1" });
			// neither component should be inside the other
			{
				Polygon poly[] = new Polygon[2];
				// assemble the two polygons (doesn't matter if they're a bit messy at places)
				for(int iComp=0;iComp<2;iComp++){
					final int NP = ((LinkedList)components.get(iComp)).size();
					int px[] = new int[NP],
						py[] = new int[NP];
					for(int i=0;i<NP;i++)
					{
						Atom a = ((Atom)((LinkedList)components.get(iComp)).get(i));
						px[i]=(int)a.pos.x;
						py[i]=(int)a.pos.y;
					}
					poly[iComp] = new Polygon(px,py,NP);
				}
				// check for either polygon having a point inside the other
				// (given that bond-crossing is forbidden, we expect this to be a complete test of separatedness)
				for(int iComp=0;iComp<2;iComp++) {
					LinkedList c = (LinkedList)components.get(iComp);
					int NP = c.size();
					for(int i=0;i<NP;i++) {
						Atom a = (Atom)c.get(i);
						// is this point inside the other polygon?
						if(poly[1-iComp].contains(a.pos))
							return Application.localize(new String[] {"levels","celldivision","error","2" });
					}
				}
			}
			
			// let's enforce that the template is a sequence of 2-connected atoms starting with 'e'
			// and ending with 'f', with each end connected to 3+ connected atoms, and only types a-d in between
			Atom heads[] = new Atom[2]; // will put the pointers to the two 'e' ends here
			int n_found=0;
			for(int i=0;i<atoms.length && n_found<2;i++) {
				Atom a = atoms[i];
				if(a.type==4 && a.state!=0 && a.bonds.size()==2) heads[n_found++]=a;
			}
			if(n_found<2)
				return Application.localize(new String[] {"levels","celldivision","error","3" });
			// each head should be in a separate component
			LinkedList c1 = (LinkedList)components.get(0),c2=(LinkedList)components.get(1);
			if( (c1.contains(heads[0]) && !c2.contains(heads[1])) || (c2.contains(heads[0]) && !c1.contains(heads[1])) )
				return Application.localize(new String[] {"levels","celldivision","error","4" });
			// work down each template, adding the type of each 2-connected atom to sequence[i]
			String sequence[] = {new String(),new String()};
			for(int iCell=0;iCell<2;iCell++) {
				LinkedList seen = new LinkedList();
				Atom current = heads[iCell];
				seen.add(current);
				sequence[iCell]="e"; // let's get things started
				if(((Atom)current.bonds.getFirst()).bonds.size()==2)
					current = (Atom)current.bonds.getFirst();
				else
					current = (Atom)current.bonds.getLast();
				while(sequence[iCell].length()<10) {
					// if the current atom has other than 2 bonds then we are done
					if(current.bonds.size()!=2) break;
					// append the type letter (a-f) to the string
					sequence[iCell] += Atom.type_code.charAt(current.type);
					// add the current atom to the list so that we will know we have seen it before
					seen.add(current);
					// move onto the next bond (we know this atom has exactly two) 
					if(seen.contains((Atom)current.bonds.getFirst()))
						current = (Atom)current.bonds.get(1);
					else
						current = (Atom)current.bonds.getFirst();
				}
				//System.out.println(sequence[iCell]);
				if(sequence[iCell].length()!=6 || sequence[iCell].charAt(0)!='e' ||
					sequence[iCell].charAt(5)!='f')	//TODO add the parameter : "Incorrect template sequence detected: "+sequence[iCell];
					return Application.localize(new String[] {"levels","celldivivion","error","5" });
			}
			if(sequence[0].compareTo(sequence[1])!=0)
				return Application.localize(new String[] {"levels","celldivision","error","6" });
				
				return null;
	}
}