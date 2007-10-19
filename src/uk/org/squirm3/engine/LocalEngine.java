package uk.org.squirm3.engine;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.DraggingPoint;
import uk.org.squirm3.data.FixedPoint;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;
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

public class LocalEngine implements IApplicationEngine {
	
	private final EngineDispatcher engineDispatcher;
	private Collider collider;
	private Thread thread;
	// simulation's attributes
	public boolean resetNeeded;
	private short sleepPeriod; // how many milliseconds to sleep for each iteration (user changeable)
	// things to do with the dragging around of atoms
	private DraggingPoint draggingPoint;
	private DraggingPoint lastUsedDraggingPoint;
	// level
	private Level currentLevel;
	private final List levelList;
	private int levelIndex;
	
	public LocalEngine() {
		// TODO values should'nt be hardcoded, properties files ?
		final int simulationWidth = 650;
		final int simulationHeight = 550;
		final int numberOfAtoms = 50;
		final Configuration configuration = new Configuration(numberOfAtoms, Level.TYPES, simulationWidth, simulationHeight);
		collider = new Collider(new Atom[0],1,1); // quick fix to avoid null pointer exception
		// challenges
		levelList = new ArrayList();
		levelList.add(new Intro(configuration));
		levelList.add(new Join_As(configuration));
		levelList.add(new Make_ECs(configuration));
		levelList.add(new Line_Cs(configuration));
		levelList.add(new Join_all(configuration));
		levelList.add(new Connect_corners(configuration));
		levelList.add(new Abcdef_chains(configuration));
		levelList.add(new Join_same(configuration));
		levelList.add(new Match_template(configuration));
		levelList.add(new Break_molecule(configuration));
		levelList.add(new Bond_prisoner(configuration));
		levelList.add(new Pass_message(configuration));
		levelList.add(new Split_ladder(configuration));
		levelList.add(new Insert_atom(configuration));
		levelList.add(new Make_ladder(configuration));
		levelList.add(new Selfrep(configuration));
		levelList.add(new Grow_membrane(configuration));
		levelList.add(new Membrane_transport(configuration));
		levelList.add(new Membrane_division(configuration));
		levelList.add(new Cell_division(configuration));
		// manager of the listeners
		engineDispatcher = new EngineDispatcher();
		resetNeeded = false;
		sleepPeriod = 50;
		// start the challenge by the introduction
		try {
			goToLevel(0, null);
		} catch(Exception e) {}
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
	
	public short getSimulationSpeed() {
		return sleepPeriod;
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
	
	public boolean restartLevel(Configuration configuration) {
		pauseSimulation();
		Atom[] atoms = currentLevel.createAtoms(configuration);
		if(atoms==null) return false;
		collider = new Collider(atoms, (int)currentLevel.getConfiguration().getWidth(),
				(int)currentLevel.getConfiguration().getHeight());
		if(configuration!=null) engineDispatcher.configurationHasChanged();
		engineDispatcher.atomsHaveChanged();
		needToRestartLevel(false);
		return true;
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
							collider.doTimeStep((int)currentLevel.getConfiguration().getWidth(),
									(int)currentLevel.getConfiguration().getHeight(), draggingPoint);
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
	
	public EngineDispatcher getEngineDispatcher() {
		return engineDispatcher;
	}
	
	public boolean goToLevel(int levelIndex, Configuration configuration) {
		this.levelIndex = levelIndex;
		currentLevel = (Level)levelList.get(levelIndex);
		pauseSimulation();
		collider.setReactions(new Reaction[0]);
		engineDispatcher.reactionsHaveChanged();
		Atom[] atoms = currentLevel.createAtoms(configuration);
		if(atoms==null) return false;
		collider = new Collider(atoms, (int)currentLevel.getConfiguration().getWidth(),
				(int)currentLevel.getConfiguration().getHeight());
		engineDispatcher.atomsHaveChanged();
		engineDispatcher.levelHasChanged();
		runSimulation();
		return true;
	}
	
	public boolean goToFirstLevel() {
		return goToLevel(0, null);
	}
	
	public boolean goToLastLevel() {
		return goToLevel(levelList.size()-1, null);
	}
	
	public boolean goToNextLevel() {
		if(levelIndex+1<levelList.size())
			return goToLevel(levelIndex+1, null);
		return false;
	}
	
	public boolean goToPreviousLevel() {
		if(levelIndex-1>=0)
			return goToLevel(levelIndex-1, null);
		return false;
	}
	
	public List getLevels() {
		return levelList;
	}
	
}

//*******************************************************************

class Intro extends Level //0
{
	
	public Intro(Configuration configuration) {
		super(Application.localize(new String[] {"levels","intro","title" }),
				Application.localize(new String[] {"levels","intro","challenge" }),
				Application.localize(new String[] {"levels","intro","title" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		else return null;
	}
	
	public String evaluate(Atom[] atoms) { return null; } // Is this one called even one time ???
}


//*******************************************************************

class Join_As extends Level //1
{
	
	public Join_As(Configuration configuration) {
		super(Application.localize(new String[] {"levels","joinas","title" }),
				Application.localize(new String[] {"levels","joinas","challenge" }),
				Application.localize(new String[] {"levels","joinas","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		else return null;
	}
	
	public String evaluate(Atom[] atoms) {
		// is any non-'a' atom bonded with any other?
		for(int i=0;i<atoms.length;i++)
			if(atoms[i].getType()!=0 && atoms[i].getBonds().size()>0)
				return Application.localize(new String[] {"levels","joinas","error","1" });
		// is every 'a' atom bonded together in a big clump?
		LinkedList a_atoms = new LinkedList();
		for(int i=0;i<atoms.length;i++) {
			if(atoms[i].getType()==0) {
				// this will do as our starting point
				atoms[i].getAllConnectedAtoms(a_atoms);
				break;
			}
		}
		for(int i=0;i<atoms.length;i++) 
			if(atoms[i].getType()==0 && !a_atoms.contains(atoms[i]))
				return Application.localize(new String[] {"levels","joinas","error","2" });
		return null;
	}
}

//*******************************************************************

class Make_ECs extends Level //2
{
	
	public Make_ECs(Configuration configuration) {
		super(Application.localize(new String[] {"levels","makeecs","title" }),
				Application.localize(new String[] {"levels","makeecs","challenge" }),
				Application.localize(new String[] {"levels","makeecs","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		else return null;
	}
	
	public String evaluate(Atom[] atoms) {
		// each atom must be either 'e' and bonded to just a 'c' (or vice versa), or unbonded
		int ec_pairs_found=0,loose_e_atoms_found=0,loose_c_atoms_found=0;
		for(int i=0;i<atoms.length;i++) {
			Atom atom = atoms[i];
			if(atom.getType()!=2 && atom.getType()!=4 && atom.getBonds().size()!=0)
				return Application.localize(new String[] {"levels","makeecs","error","1" });
			if(atom.getType()==2 || atom.getType()==4) {
				if(atom.getBonds().size()>1)
					return Application.localize(new String[] {"levels","makeecs","error","2" });
				if(atom.getBonds().size()==0) {
					if(atom.getType()==2) loose_c_atoms_found++;
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
	private Atom seed;
	
	public Line_Cs(Configuration configuration) {
		super(Application.localize(new String[] {"levels","linecs","title" }),
				Application.localize(new String[] {"levels","linecs","challenge" }),
				Application.localize(new String[] {"levels","linecs","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
			for(int i = 0; i < atoms.length; i++) {
				if(atoms[i].getType()==2) {
					seed = atoms[i];
					seed.setState(1);
					return atoms;
				}
			}
		}
		return null;
	}
	
	public String evaluate(Atom[] atoms) {
		int single_bonded_atoms_found=0,double_bonded_atoms_found=0;
		// get the set of atoms joined to atom[0]
		LinkedList joined = new LinkedList();
		seed.getAllConnectedAtoms(joined);
		// fail on bonds<1 or >2, or not in 'joined' list
		for(int i=0;i<atoms.length;i++) {
			Atom atom = atoms[i];
			if(atom.getType()!=2) {
				if(atom.getBonds().size()!=0) 
					return Application.localize(new String[] {"levels","linecs","error","1" });;
					continue; // no other tests for non-'c' atoms
			}
			if(atom.getBonds().size()==1) single_bonded_atoms_found++;
			else if(atom.getBonds().size()==2) double_bonded_atoms_found++;
			else if(atom.getBonds().size()==0)
				return Application.localize(new String[] {"levels","linecs","error","2" });
			else
				return Application.localize(new String[] {"levels","linecs","error","3" });
			if(!joined.contains(atom))
				return Application.localize(new String[] {"levels","linecs","error","4" });
		}
		// one final check on chain configuration
		if(single_bonded_atoms_found!=2)
			return Application.localize(new String[] {"levels","linecs","error","5" });
		return null;
	}
}

//*******************************************************************

class Join_all extends Level //4
{
	
	public Join_all(Configuration configuration) {
		super(Application.localize(new String[] {"levels","joinall","title" }),
				Application.localize(new String[] {"levels","joinall","challenge" }),
				Application.localize(new String[] {"levels","joinall","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		else return null;
	}
	
	public String evaluate(Atom[] atoms) {
		// all joined?
		LinkedList joined = new LinkedList();
		atoms[0].getAllConnectedAtoms(joined);
		if(joined.size()!=atoms.length)
			return Application.localize(new String[] {"levels","joinall","error","1" });
		return null;
	}
}

//*******************************************************************

class Connect_corners extends Level //5
{
	
	public Connect_corners(Configuration configuration) {
		super(Application.localize(new String[] {"levels","connectcorners","title" }),
				Application.localize(new String[] {"levels","connectcorners","challenge" }),
				Application.localize(new String[] {"levels","connectcorners","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		atoms[0] = new Atom(new FixedPoint(size*1.5f, size*1.5f),5,1);
		atoms[1] = new Atom(new FixedPoint(configuration.getWidth()-size*1.5f, configuration.getHeight()-size*1.5f),3,1);
		if(createAtoms(configuration.getNumberOfAtoms()-2 , configuration.getTypes(), 0, configuration.getWidth(), 2*size, configuration.getHeight()-2*size, atoms)) return atoms;
		return null;
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
	
	public Abcdef_chains(Configuration configuration) {
		super(Application.localize(new String[] {"levels","abcdefchains","title" }),
				Application.localize(new String[] {"levels","abcdefchains","challenge" }),
				Application.localize(new String[] {"levels","abcdefchains","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		else return null;
	}
	
	public String evaluate(Atom[] atoms) {
		// how many abcdef chains are there?
		int num_abcdef_chains_found=0;
		for(int i=0;i<atoms.length;i++) {
			Atom a = atoms[i];
			if(a.getType()==0 && a.getBonds().size()==1) {
				// looks promising - let's check
				LinkedList joined = new LinkedList();
				a.getAllConnectedAtoms(joined);
				if(joined.size()!=6) continue;
				Iterator it = joined.iterator();
				if(((Atom)it.next()).getType()==0 && ((Atom)it.next()).getType()==1
						&& ((Atom)it.next()).getType()==2 && ((Atom)it.next()).getType()==3
						&& ((Atom)it.next()).getType()==4 && ((Atom)it.next()).getType()==5)
					num_abcdef_chains_found++;
				// (this isn't a perfect test but hopefully close enough)
			}
		}
		if(num_abcdef_chains_found==0)
			return Application.localize(new String[] {"levels","abcdefchains","error","1" });
		else if(num_abcdef_chains_found==1)
			return Application.localize(new String[] {"levels","abcdefchains","error","1" });
		return null;
	}
}

//*******************************************************************

class Join_same extends Level //7
{
	
	public Join_same(Configuration configuration) {
		super(Application.localize(new String[] {"levels","joinsame","title" }),
				Application.localize(new String[] {"levels","joinsame","challenge" }),
				Application.localize(new String[] {"levels","joinsame","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		else return null;
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
				if(other.getType() != atom.getType())
					return Application.localize(new String[] {"levels","joinsame","error","1" });
			}
			// are there any atoms of the same type not on this list?
			for(int j=0;j<atoms.length;j++)
				if(atoms[j].getType() == atom.getType() && !joined.contains(atoms[j]))
					return Application.localize(new String[] {"levels","joinsame","error","1" });
		}
		return null;
	}
}

//*******************************************************************

class Match_template extends Level //8
{
	
	public Match_template(Configuration configuration) {
		super(Application.localize(new String[] {"levels","matchtemplate","title" }),
				Application.localize(new String[] {"levels","matchtemplate","challenge" }),
				Application.localize(new String[] {"levels","matchtemplate","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
		// place and bond six random atoms to form a template
		for(int i=0;i<6;i++) {
			atoms[i] = new Atom(new FixedPoint(size*1.5f, size*5.0f+i*size*2.1f),PRNG.nextInt(6), 1);
			if(i>0)
				atoms[i].bondWith(atoms[i-1]);
		}
		if(createAtoms(configuration.getNumberOfAtoms()-6 , configuration.getTypes(), 2.5f*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		return null;
	}
	
	public String evaluate(Atom[] atoms) {
		// does each atom 0-5 have another single type-matching atom attached?
		for(int i=0;i<6;i++) {
			Atom a = atoms[i];
			Atom b = (Atom)a.getBonds().getLast();
			if(b.getType()!=a.getType() || b.getBonds().size()!=1)
				return Application.localize(new String[] {"levels","matchtemplate","error","1" });
			// (not a complete test, but hopefully ok)
		}
		return null; }
}

//*******************************************************************

class Break_molecule extends Level //9
{
	
	public Break_molecule(Configuration configuration) {
		super(Application.localize(new String[] {"levels","breakmolecule","title" }),
				Application.localize(new String[] {"levels","breakmolecule","challenge" }),
				Application.localize(new String[] {"levels","breakmolecule","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		// place and bond 10 atoms to form a template
		IPhysicalPoint mobilePoint = new MobilePoint();
		for(int i=0;i<10;i++) {
			final int type = (i<5)?0:3;
			mobilePoint.setPositionX(size*1.5f);
			mobilePoint.setPositionY(size*1.5f+i*size*2.1f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, type, 1);
			if(i>0)
				atoms[i].bondWith(atoms[i-1]);
		}
		if(createAtoms(configuration.getNumberOfAtoms()-10 , configuration.getTypes(), 2.5f*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		return null;
	}
	
	public String evaluate(Atom[] atoms) {
		int n_bonds[]={1,2,2,2,1,1,2,2,2,1};
		for(int i=0;i<10;i++)
			if(atoms[i].getBonds().size() != n_bonds[i])
				return Application.localize(new String[] {"levels","breakmolecule","error","1" });
		return null; }
}

//*******************************************************************

class Bond_prisoner extends Level //10
{
	private Atom prisoner;
	
	public Bond_prisoner(Configuration configuration) {
		super(Application.localize(new String[] {"levels","bondprisoner","title" }),
				Application.localize(new String[] {"levels","bondprisoner","challenge" }),
				Application.localize(new String[] {"levels","bondprisoner","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		prisoner = null;
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		// place and bond 8 atoms to form a loop
		IPhysicalPoint mobilePoint = new MobilePoint();
		int pos_x[]={-1,0,1,1,1,0,-1,-1};
		int pos_y[]={-1,-1,-1,0,1,1,1,0};
		for(int i=0;i<8;i++) {
			int state;
			if(i==0) state = 3;
			else if(i==1) state = 4;
			else state = 2;
			mobilePoint.setPositionX(size*4.0f + pos_x[i]*size*2.0f);
			mobilePoint.setPositionY(size*7.0f + pos_y[i]*size*2.0f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, 0, state);
		}
		for(int i=0;i<8;i++) atoms[i].bondWith(atoms[(i+1)%8]);
		// add the prisoner (f1)
		mobilePoint.setPositionX(size*4.0f);
		mobilePoint.setPositionY(size*7.0f);
		atoms[8] = new Atom(mobilePoint, 5, 1);
		// create the others atoms
		if(createAtoms(configuration.getNumberOfAtoms()-9 , configuration.getTypes(), 7*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
			for(int i = 0; i < atoms.length; i++) {
				if(atoms[i].getType()==5) {
					prisoner = atoms[i];
					return atoms;
				}
			}
		}
		return null;
	}
	
	public String evaluate(Atom[] atoms) {
		// is the 'prisoner' atom bonded with an f?
		if(prisoner.getBonds().size()==0)
			return Application.localize(new String[] {"levels","bondprisonner","error","1" });
		if(((Atom)atoms[8].getBonds().getFirst()).getType()!=5)
			return Application.localize(new String[] {"levels","bondprisonner","error","2" });
		return null; }
}

//*******************************************************************

class Pass_message extends Level //11
{
	
	public Pass_message(Configuration configuration) {
		super(Application.localize(new String[] {"levels","passmessage","title" }),
				Application.localize(new String[] {"levels","passmessage","challenge" }),
				Application.localize(new String[] {"levels","passmessage","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		// place and bond 10 atoms to form a template
		Random PRNG = new Random(); // a prng for use when resetting atoms
		IPhysicalPoint mobilePoint = new MobilePoint();
		for(int i=0;i<10;i++) {
			final int state = (i==0)?2:1;
			mobilePoint.setPositionX(size*1.5f);
			mobilePoint.setPositionY(size*1.5f+i*size*2.1f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, PRNG.nextInt(6), state);
			if(i>0)
				atoms[i].bondWith(atoms[i-1]);
		}
		if(createAtoms(configuration.getNumberOfAtoms()-10 , configuration.getTypes(), 2.5f*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		return null;
	}
	
	public String evaluate(Atom[] atoms) {
		int n_bonds[]={1,2,2,2,2,2,2,2,2,1};
		for(int i=0;i<10;i++)
			if(atoms[i].getBonds().size() != n_bonds[i])
				return Application.localize(new String[] {"levels","passmessage","error","1" });
			else if(atoms[i].getState() != 2)
				return Application.localize(new String[] {"levels","passmessage","error","2" });
		return null; }
}

//*******************************************************************

class Split_ladder extends Level //12
{
	
	public Split_ladder(Configuration configuration) {
		super(Application.localize(new String[] {"levels","splitladder","title" }),
				Application.localize(new String[] {"levels","splitladder","challenge" }),
				Application.localize(new String[] {"levels","splitladder","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		// place and bond 20 atoms to form a template
		Random PRNG = new Random(); // a prng for use when resetting atoms
		IPhysicalPoint mobilePoint = new MobilePoint();
		for(int i=0;i<20;i++) {
			final int state = (i==0 || i==10)?2:1;
			final int type = (i<10)?(PRNG.nextInt(6)):atoms[i-10].getType();
			final float x = (i<10)?size*1.5f:size*3.7f;
			final float y = (i<10)?(size*3+i*size*2.1f):(size*3+(i-10)*size*2.1f);
			mobilePoint.setPositionX(x);
			mobilePoint.setPositionY(y);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, type, state);
			if(i!=0 && i!=10)
				atoms[i].bondWith(atoms[i-1]);
			if(i>=10)
				atoms[i].bondWith(atoms[i-10]);
		}
		if(createAtoms(configuration.getNumberOfAtoms()-20 , configuration.getTypes(), 4.5f*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		return null;
	}
	
	public String evaluate(Atom[] atoms) {
		int n_bonds[]={1,2,2,2,2,2,2,2,2,1};
		for(int i=0;i<20;i++)
			if(atoms[i].getBonds().size() != n_bonds[i%10])
				return Application.localize(new String[] {"levels","splitladder","error","1" });
		return null; }
}

//*******************************************************************

class Insert_atom extends Level //13
{
	
	public Insert_atom(Configuration configuration) {
		super(Application.localize(new String[] {"levels","insertatom","title" }),
				Application.localize(new String[] {"levels","insertatom","challenge" }),
				Application.localize(new String[] {"levels","insertatom","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		// place and bond 10 atoms to form a template
		IPhysicalPoint mobilePoint = new MobilePoint();
		for(int i=0;i<10;i++) {
			final int state;
			if(i==4) state = 2;
			else if(i==5) state = 3;
			else state =1;
			mobilePoint.setPositionX(size*1.5f);
			mobilePoint.setPositionY(size*3.0f+i*size*2.1f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, 4, state);
			if(i>0)
				atoms[i].bondWith(atoms[i-1]);
		}
		if(createAtoms(configuration.getNumberOfAtoms()-10 , configuration.getTypes(), 2.5f*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		return null;

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
			if(a.getBonds().size()!=n_bonds[i])
				return Application.localize(new String[] {"levels","insertatom","error","2" });
			if( a.getType()!=types[i])
				return Application.localize(new String[] {"levels","insertatom","error","3" });
			i++;
		}
		return null; }
}

//*******************************************************************

class Make_ladder extends Level //14
{
	
	public Make_ladder(Configuration configuration) {
		super(Application.localize(new String[] {"levels","makeladder","title" }),
				Application.localize(new String[] {"levels","makeladder","challenge" }),
				Application.localize(new String[] {"levels","makeladder","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		// place and bond 6 atoms to form a template
		Random PRNG = new Random(); // a prng for use when resetting atoms
		IPhysicalPoint mobilePoint = new MobilePoint();
		for(int i=0;i<6;i++) {
			int type;
			if(i==0) type = 4; // 'e' at the top
			else if(i==5) type = 5; // 'f' at the bottom
			else type = PRNG.nextInt(4); // 'a'-'d'
			mobilePoint.setPositionX(size*1.5f);
			mobilePoint.setPositionY(size*6.0f+i*size*2.1f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, type, 1);
			if(i>0)
				atoms[i].bondWith(atoms[i-1]);
		}
		if(createAtoms(configuration.getNumberOfAtoms()-6 , configuration.getTypes(), 2.5f*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		return null;
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
		for(int i=0;i<6;i++) original_type_count[atoms[i].getType()]++;
		Iterator it = joined.iterator();
		while(it.hasNext()) new_type_count[((Atom)it.next()).getType()]++;
		for(int i=0;i<6;i++)
			if(new_type_count[i] != original_type_count[i]*2) 
				return Application.localize(new String[] {"levels","makeladder","error","3" });
		it = joined.iterator();
		while(it.hasNext()) { 
			Atom a = (Atom)it.next();
			if(a.getType()==4 || a.getType()==5) // 'e' and 'f' 
				if(a.getBonds().size()!=2) 
					return Application.localize(new String[] {"levels","makeladder","error","4" });
				else if(a.getBonds().size()!=3)  
					return Application.localize(new String[] {"levels","makeladder","error","5" });
		}
		return null; }
}

//*******************************************************************

class Selfrep extends Level //15
{
	
	public Selfrep(Configuration configuration) {
		super(Application.localize(new String[] {"levels","selfrep","title" }),
				Application.localize(new String[] {"levels","selfrep","challenge" }),
				Application.localize(new String[] {"levels","selfrep","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
		// (ensure each type is used once (to allow multiple copies to be made))
		int[][] genomes = {{0,1,3,2},{3,1,2,0},{2,3,0,1},{1,2,0,3},{0,3,2,1}};
		int which_genome = PRNG.nextInt(5); 
		IPhysicalPoint mobilePoint = new MobilePoint();
		for(int i=0;i<6;i++) {
			final int type;
			if(i==0) type = 4; // 'e' at the top
			else if(i==5) type = 5; // 'f' at the bottom
			else type = genomes[which_genome][i-1];
			mobilePoint.setPositionX(size*1.5f);
			mobilePoint.setPositionY(size*6.0f+i*size*2.1f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, type, 1);
			if(i>0)
				atoms[i].bondWith(atoms[i-1]);
		}
		if(createAtoms(configuration.getNumberOfAtoms()-6 , configuration.getTypes(), 2.5f*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
		return null;
	}
	
	public String evaluate(Atom[] atoms) { // improved code from Ralph Hartley
		LinkedList joined = new LinkedList();
		// there should be at least two bonded 'e' atoms in the world, each at the head of a copy
		int n_found = 0;
		int bound_atoms = 0;
		for(int i=0;i<atoms.length;i++) { // include the original
			Atom first = atoms[i];
			if(first.getBonds().size()>0) {
				bound_atoms++;
				
				if(first.getType()==4) {
					joined.clear();
					first.getAllConnectedAtoms(joined);
					
					if(joined.size()!=6)
						return Application.localize(new String[] {"levels","selfrep","error","1" });
					
					Atom last = (Atom)joined.getLast();
					if (first.getBonds().size()!=1 || last.getBonds().size()!=1 || last.getType()!=5)
						return Application.localize(new String[] {"levels","selfrep","error","2" });
					
					for(int j=1;j<joined.size()-1;j++) {
						Atom a = (Atom)joined.get(j);
						if (a.getBonds().size()!=2)
							return Application.localize(new String[] {"levels","selfrep","error","3" });				
						if(a.getType() != atoms[j].getType())
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
	
	public Grow_membrane(Configuration configuration) {
		super(Application.localize(new String[] {"levels","growmembrane","title" }),
				Application.localize(new String[] {"levels","growmembrane","challenge" }),
				Application.localize(new String[] {"levels","growmembrane","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		final IPhysicalPoint mobilePoint = new MobilePoint();
		// place and bond 8 atoms to form a loop
		int pos_x[]={-1,0,1,1,1,0,-1,-1};
		int pos_y[]={-1,-1,-1,0,1,1,1,0};
		for(int i=0;i<8;i++) {
			int state;
			if(i==0) state = 3;
			else if(i==1) state = 4;
			else state = 2;
			mobilePoint.setPositionX(size*4.0f + pos_x[i]*size*2.0f);
			mobilePoint.setPositionY(size*7.0f + pos_y[i]*size*2.0f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, 0, state);
		}
		for(int i=0;i<8;i++) atoms[i].bondWith(atoms[(i+1)%8]);
		// add the prisoner (f1)
		mobilePoint.setPositionX(size*4.0f);
		mobilePoint.setPositionY(size*7.0f);
		atoms[8] = new Atom(mobilePoint, 5, 1);
		// create the others atoms
		if(createAtoms(configuration.getNumberOfAtoms()-9 , configuration.getTypes(), 7*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
			for(int i = 0; i < atoms.length; i++)
				if(atoms[i].getType()==5) return atoms;
		}
		return null;
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
			if(a.getType()!=0 || a.getBonds().size()!=2)
				return Application.localize(new String[] {"levels","growmembrane","error","1" });
			x_points[i] = (int)a.getPhysicalPoint().getPositionX(); // (need these for polygon check, below)
			y_points[i] = (int)a.getPhysicalPoint().getPositionY();
			i++;
		}
		// inside the polygon formed by the a atoms there should be exactly one atom - the original f1 (although its state may have changed)
		Atom f1 = atoms[8]; // see the setup code for this level
		Polygon poly = new Polygon(x_points,y_points,joined.size());
		if(!poly.contains(new Point2D.Float(f1.getPhysicalPoint().getPositionX(),f1.getPhysicalPoint().getPositionY())))
			return Application.localize(new String[] {"levels","growmembrane","error","2" });
		// and no other 'a' atoms around
		for(i=0;i<atoms.length;i++) {
			Atom a = atoms[i];
			if(!joined.contains(a) && a.getType()==0)
				return Application.localize(new String[] {"levels","growmembrane","error","3" });
		}
		return null; }
}

//*******************************************************************

class Membrane_transport extends Level //17
{
	
	public Membrane_transport(Configuration configuration) {
		super(Application.localize(new String[] {"levels","membranetransport","title" }),
				Application.localize(new String[] {"levels","membranetransport","challenge" }),
				Application.localize(new String[] {"levels","membranetransport","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		final IPhysicalPoint mobilePoint = new MobilePoint();
		// place and bond N atoms to form a loop
		final int N=12;
		int pos_y[]={-1,0,1,2,3,3,3,2,1,0,-1,-1}; // reading clockwise from the top-left corner (y is down)
		int pos_x[]={-1,-1,-1,-1,-1,0,1,1,1,1,1,0};
		int i; // atom index incremented in loops but also used elsewhere without resetting
		for(i=0;i<N;i++) {
			int state;
			if(i==0) state = 3;
			else if(i==1) state = 4;
			else state = 2;
			mobilePoint.setPositionX(size*4.0f + pos_x[i]*size*2.0f);
			mobilePoint.setPositionY(size*7.0f + pos_y[i]*size*2.0f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, 0, state);
		}
		for(int j=0;j<N;j++) atoms[j].bondWith(atoms[(j+1)%N]);
		// put two e1 atoms and one b1 atom inside
		int so_far=i;
		for(;i<so_far+3;i++) {
			mobilePoint.setPositionY(size*(7.0f+(i-so_far)*2.0f));
			mobilePoint.setPositionX(size*4.0f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, (i-so_far)==0?1:4, 1);
		}
		// create the others atoms
		if(createAtoms(configuration.getNumberOfAtoms()-(N+1) , configuration.getTypes(), 7*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) 
			return atoms;
		return null;

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
			if(a.getType()!=0 || a.getBonds().size()!=2)
				return Application.localize(new String[] {"levels","membranetransport","error","1" });
			x_points[i] = (int)a.getPhysicalPoint().getPositionX(); // (need these for polygon check, below)
			y_points[i] = (int)a.getPhysicalPoint().getPositionY();
			i++;
		}
		// inside should be the original 'b' atom, and all the 'f' atoms, and nothing else
		Atom b1 = atoms[12]; // see the setup code for this level
		Polygon poly = new Polygon(x_points,y_points,joined.size());
		if(!poly.contains(new Point2D.Float(b1.getPhysicalPoint().getPositionX(),b1.getPhysicalPoint().getPositionY())))
			return Application.localize(new String[] {"levels","membranetransport","error","2" });
		// check the other atoms (want: f's inside, other's outside)
		for(i=joined.size()+1;i<atoms.length;i++) {
			Atom a = atoms[i];
			if(a.getType()==5 && !poly.contains(new Point2D.Float(a.getPhysicalPoint().getPositionX(),a.getPhysicalPoint().getPositionY())))
				return Application.localize(new String[] {"levels","membranetransport","error","3" });
			else if(a.getType()!=5 && poly.contains(new Point2D.Float(a.getPhysicalPoint().getPositionX(),a.getPhysicalPoint().getPositionY())))
				return Application.localize(new String[] {"levels","membranetransport","error","4" });
		}
		return null; }
}

//*******************************************************************

class Membrane_division extends Level //18
{
	
	public Membrane_division(Configuration configuration) {
		super(Application.localize(new String[] {"levels","membranedivision","title" }),
				Application.localize(new String[] {"levels","membranedivision","challenge" }),
				Application.localize(new String[] {"levels","membranedivision","hint" }),
				configuration);
	}
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		final IPhysicalPoint mobilePoint = new MobilePoint();
		// place and bond N atoms to form a loop
		final int N=12;
		int pos_y[]={-1,0,1,2,3,3,3,2,1,0,-1,-1}; // reading clockwise from the top-left corner (y is down)
		int pos_x[]={-1,-1,-1,-1,-1,0,1,1,1,1,1,0};
		for(int i=0;i<N;i++) {
			int state;
			if(i==0) state = 3;
			else if(i==N/2) state = 4;
			else state = 2;
			mobilePoint.setPositionX(size*4.0f + pos_x[i]*size*2.0f);
			mobilePoint.setPositionY(size*7.0f + pos_y[i]*size*2.0f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, 0, state);
		}
		for(int j=0;j<N;j++) atoms[j].bondWith(atoms[(j+1)%N]);
		// create the others atoms
		if(createAtoms(configuration.getNumberOfAtoms()-N , configuration.getTypes(), 7*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) 
			return atoms;
		return null;
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
				if(a.getType()!=0 || a.getBonds().size()!=2)
					return Application.localize(new String[] {"levels","membranedivision","error","3" });
			}
		}
		return null; }
}

//*******************************************************************

class Cell_division extends Level //19
{
	
	public Cell_division(Configuration configuration) {
		super(Application.localize(new String[] {"levels","celldivision","title" }),
				Application.localize(new String[] {"levels","celldivision","challenge" }),
				Application.localize(new String[] {"levels","celldivision","hint" }),
				configuration);
	}
	
	
	protected Atom[] createAtoms_internal(Configuration configuration){
		Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
		final float size = Atom.getAtomSize();
		Random PRNG = new Random(); // a prng for use when resetting atoms
		// place and bond N atoms to form a loop
		final int N=18;
		int pos_y[]={-1,0,1,2,3,4,5,6,6,6,5,4,3,2,1,0,-1,-1}; // reading clockwise from the top-left corner (y is down)
		int pos_x[]={-1,-1,-1,-1,-1,-1,-1,-1,0,1,1,1,1,1,1,1,1,0};
		int i; // atom index incremented in loops but also used elsewhere without resetting
		IPhysicalPoint mobilePoint = new MobilePoint();
		for(i=0;i<N;i++) {
			final int state = (i==N-1 || i==N/2-1)?3:2;
			mobilePoint.setPositionX(size*4.0f + pos_x[i]*size*2.0f);
			mobilePoint.setPositionY(size*7.0f + pos_y[i]*size*2.0f);
			Level.setRandomSpeed(mobilePoint);
			atoms[i] = new Atom(mobilePoint, 0, state);

		}
		for(int j=0;j<N;j++) atoms[j].bondWith(atoms[(j+1)%N]);
		
		
		// place and bond six atoms to form a template
		// (ensure each type is used once (to allow multiple copies to be made, if they want))
		int[][] genomes = {{0,1,3,2},{3,1,2,0},{2,3,0,1},{1,2,0,3},{0,3,2,1}};
		int which_genome = PRNG.nextInt(5); 
		int so_far=i;
		for(;i<so_far+6;i++) {
			mobilePoint.setPositionX(size*4.0f);
			mobilePoint.setPositionY(size*7.0f+(i-so_far)*size*2.0f);
			if(i-so_far==0) {
				atoms[i] = new Atom(mobilePoint, 4, 1); // 'e' at the top
				atoms[i].bondWith(atoms[N-1]);
			} else if(i-so_far==5) {
				atoms[i] = new Atom(mobilePoint, 5, 1); // 'f' at the bottom
				atoms[i].bondWith(atoms[N/2-1]);
				atoms[i].bondWith(atoms[i-1]);
			} else {
				atoms[i] = new Atom(mobilePoint, genomes[which_genome][(i-so_far)-1], 1);
				atoms[i].bondWith(atoms[i-1]);
			}
		}
		// set one of the free-floating atoms to be a killer enzyme we must exclude from the cell
		
		if(createAtoms(configuration.getNumberOfAtoms()-(N+6) , new int[] {0, 1, 2, 0, 3, 4, 0, 5}, 6*size, configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
			atoms[atoms.length-1] = new Atom(atoms[atoms.length-1].getPhysicalPoint(), Atom.KILLER_TYPE, 0);
			return atoms;
		}
		return null;
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
					px[i]=(int)a.getPhysicalPoint().getPositionX();
					py[i]=(int)a.getPhysicalPoint().getPositionY();
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
					if(poly[1-iComp].contains(new Point2D.Float(a.getPhysicalPoint().getPositionX(),a.getPhysicalPoint().getPositionY())))
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
			if(a.getType()==4 && a.getState()!=0 && a.getBonds().size()==2) heads[n_found++]=a;
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
			if(((Atom)current.getBonds().getFirst()).getBonds().size()==2)
				current = (Atom)current.getBonds().getFirst();
			else
				current = (Atom)current.getBonds().getLast();
			while(sequence[iCell].length()<10) {
				// if the current atom has other than 2 bonds then we are done
				if(current.getBonds().size()!=2) break;
				// append the type letter (a-f) to the string
				sequence[iCell] += Atom.type_code.charAt(current.getType());
				// add the current atom to the list so that we will know we have seen it before
				seen.add(current);
				// move onto the next bond (we know this atom has exactly two) 
				if(seen.contains((Atom)current.getBonds().getFirst()))
					current = (Atom)current.getBonds().get(1);
				else
					current = (Atom)current.getBonds().getFirst();
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