package uk.org.squirm3.engine;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.DraggingPoint;
import uk.org.squirm3.data.FixedPoint;
import uk.org.squirm3.data.IPhysicalPoint;
import uk.org.squirm3.data.Level;
import uk.org.squirm3.data.MobilePoint;

/**  
${my.copyright}
 */

public class ApplicationEngine {

    // things to run the collider and the commands
    private Collider collider;
    private Thread applicationThread;
    private final Object colliderExecution;
    private boolean isRunning;
    private short sleepPeriod;
    private LinkedList commands;
    // things to do with the dragging around of atoms
    private DraggingPoint draggingPoint;
    private DraggingPoint lastUsedDraggingPoint;
    // composition
    private LevelManager levelManager;
    private ReactionManager reactionManager;
    private final EngineDispatcher engineDispatcher;

    public ApplicationEngine() throws Exception {
        final int simulationWidth = Integer.parseInt(Application.getProperty("configuration.simulation.width"));
        final int simulationHeight = Integer.parseInt(Application.getProperty("configuration.simulation.height"));
        final int numberOfAtoms = Integer.parseInt(Application.getProperty("configuration.simulation.atom.number"));

        final Configuration configuration = new Configuration(numberOfAtoms, Level.TYPES, simulationWidth, simulationHeight);

        // load levels
        levelManager = new LevelManager();
        int i = 0;
        while(true) {
            String className = Application.getProperty("configuration.levels."+new Integer(i)+".class");
            if(className.equals("configuration.levels."+new Integer(i)+".class")) break;
            Class c = Class.forName(className);
            String key = Application.getProperty("configuration.levels."+new Integer(i)+".key");
            if(key.equals("configuration.levels."+new Integer(i)+".key")) break;
            Constructor[] cs = c.getConstructors();
            final String title = Application.getProperty("levels."+key+".title");
            final String texte = Application.getProperty("levels."+key+".challenge");
            final String hint = Application.getProperty("levels."+key+".hint");
            int nErrors = Integer.parseInt(Application.getProperty("levels."+key+".errors"));
            String[] errors = new String[nErrors];
            for (int j = 1; j <= nErrors; j++) {
                errors[j-1] = Application.getProperty("levels."+key+".error."+new Integer(j));
            }
            Object[] os = new Object[] {title,texte,hint,errors,configuration};
            Level l = (Level) cs[0].newInstance(os);
            levelManager.addLevel(l);
            i++;
        }
        reactionManager = new ReactionManager();
        // manager of the listeners
        engineDispatcher = new EngineDispatcher();
        sleepPeriod = 50;
        // start the challenge by the introduction
        try {
            setLevel(0, null);
        } catch(Exception e) {}
        commands = new LinkedList();
        colliderExecution = new Object();
        isRunning = true;
        // create and run the thread of this application
        applicationThread = new Thread(
                new Runnable(){
                    public void run()  {
                        while (applicationThread == Thread.currentThread()) {
                            // execute commands
                            synchronized(commands) {
                                while(!commands.isEmpty()) {
                                    ICommand command = (ICommand) commands.removeFirst();
                                    command.execute();
                                }
                            }
                            // compute one step of the simulation
                            synchronized(colliderExecution) {
                                if(isRunning) {
                                    lastUsedDraggingPoint = draggingPoint;
                                    collider.doTimeStep(draggingPoint, new LinkedList(reactionManager.getReactions()));
                                    engineDispatcher.atomsHaveChanged();
                                    try {
                                        Thread.sleep(sleepPeriod);
                                    } catch (InterruptedException e) { break; }
                                } else {
                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) { break; }
                                }

                            }

                        }
                    }
                });
        applicationThread.setPriority(Thread.MIN_PRIORITY);
        applicationThread.start();
    }

    public void clearReactions() {
        addCommand(new ICommand() {
            public void execute() {
                if(!reactionManager.getReactions().isEmpty()) {
                    reactionManager.clearReactions();
                    engineDispatcher.reactionsHaveChanged();
                }
            }
        });
    }

    //TODO avoid to create a copy per call
    public Collection getAtoms() {
        synchronized(colliderExecution) {
            Atom[] atoms = collider.getAtoms();
            List list = new LinkedList();
            for(int i = 0 ; i < atoms.length ; i++) {
                list.add(atoms[i]);
            }
            return list;
        }
    }

    public DraggingPoint getCurrentDraggingPoint() {
        return draggingPoint;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public DraggingPoint getLastUsedDraggingPoint() {
        return lastUsedDraggingPoint;
    }

    public Collection getReactions() {
        return reactionManager.getReactions();
    }

    public short getSimulationSpeed() {
        return sleepPeriod;
    }

    public void pauseSimulation() {
        addCommand(new ICommand() {
            public void execute() {
                synchronized(colliderExecution) {
                    if(isRunning) {
                        isRunning = false;
                        engineDispatcher.simulationStateHasChanged();
                    }
                }
            }
        });
    }

    public void addReactions(final Collection reactions) {
        addCommand(new ICommand() {
            public void execute() {
                reactionManager.addReactions(reactions);
                engineDispatcher.reactionsHaveChanged();
            }
        });
    }

    public void removeReactions(final Collection reactions) {
        addCommand(new ICommand() {
            public void execute() {
                reactionManager.removeReactions(reactions);
                engineDispatcher.reactionsHaveChanged();
            }
        });
    }

    public void restartLevel(final Configuration configuration) {
        addCommand(new ICommand() {
            public void execute() {
                Level currentLevel = levelManager.getCurrentLevel();
                Atom[] atoms = currentLevel.createAtoms(configuration);
                if(atoms==null) return;
                collider = new Collider(atoms, (int)currentLevel.getConfiguration().getWidth(),
                        (int)currentLevel.getConfiguration().getHeight());
                if(configuration!=null) engineDispatcher.configurationHasChanged();
                engineDispatcher.atomsHaveChanged();
                synchronized(colliderExecution) {
                    if(!isRunning) {
                        isRunning = true;
                        engineDispatcher.simulationStateHasChanged();
                    }
                }
            }
        });
    }

    public void runSimulation() {
        addCommand(new ICommand() {
            public void execute() {
                synchronized(colliderExecution) {
                    if(!isRunning) {
                        isRunning = true;
                        engineDispatcher.simulationStateHasChanged();
                    }
                }
            }
        });
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

    public void setReactions(final Collection reactions) {
        addCommand(new ICommand() {
            public void execute() {
                reactionManager.clearReactions();
                reactionManager.addReactions(reactions);
                engineDispatcher.reactionsHaveChanged();
            }
        });
    }

    public void setSimulationSpeed(final short newSleepPeriod) {
        addCommand(new ICommand() {
            public void execute() {
                sleepPeriod = newSleepPeriod;
                engineDispatcher.simulationSpeedHasChanged();
            }
        });
    }

    public boolean simulationIsRunning() {
        return isRunning;
    }

    public EngineDispatcher getEngineDispatcher() {
        return engineDispatcher;
    }

    private void setLevel(int levelIndex, Configuration configuration) {
        levelManager.setLevel(levelIndex);
        if(collider!=null) {
            reactionManager.clearReactions();
            engineDispatcher.reactionsHaveChanged();
        }
        Level currentLevel = levelManager.getCurrentLevel();
        Atom[] atoms = currentLevel.createAtoms(configuration);
        if(atoms==null) return;
        collider = new Collider(atoms, (int)currentLevel.getConfiguration().getWidth(),
                (int)currentLevel.getConfiguration().getHeight());
        engineDispatcher.atomsHaveChanged();
        engineDispatcher.levelHasChanged();
        return;
    }

    public void goToLevel(final int levelIndex, final Configuration configuration) {
        addCommand(new ICommand() {
            public void execute() {
                setLevel(levelIndex, configuration);
            }
        });
    }

    public void goToFirstLevel() { goToLevel(0, null); }

    public void goToLastLevel() {
        addCommand(new ICommand() {
            public void execute() {
                setLevel(levelManager.getNumberOfLevel()-1, null);
            }
        });
    }

    public void goToNextLevel() {
        addCommand(new ICommand() {
            public void execute() {
                int levelIndex = levelManager.getCurrentLevelIndex();
                if(levelIndex+1<levelManager.getNumberOfLevel()) setLevel(levelIndex+1, null);
            }
        });
    }

    public void goToPreviousLevel() {
        addCommand(new ICommand() {
            public void execute() {
                int levelIndex = levelManager.getCurrentLevelIndex();
                if(levelIndex-1>=0) setLevel(levelIndex-1, null);
            }
        });
    }

    private interface ICommand {
        public void execute();
    }

    private void addCommand(ICommand c) {
        synchronized(commands) {
            commands.add(c);
        }
    }

}

//*******************************************************************

class Intro extends Level //0
{

    public Intro(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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

    public Join_As(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration){
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        if(createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms)) return atoms;
        else return null;
    }

    public String evaluate(Atom[] atoms) {
        // is any non-'a' atom bonded with any other?
        for(int i=0;i<atoms.length;i++)
            if(atoms[i].getType()!=0 && atoms[i].getBonds().size()>0) return getError(1);
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
            if(atoms[i].getType()==0 && !a_atoms.contains(atoms[i])) return getError(2);
        return null;
    }
}

//*******************************************************************

class Make_ECs extends Level //2
{

    public Make_ECs(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
            if(atom.getType()!=2 && atom.getType()!=4 && atom.getBonds().size()!=0) return getError(1);
            if(atom.getType()==2 || atom.getType()==4) {
                if(atom.getBonds().size()>1) return getError(2);
                if(atom.getBonds().size()==0) {
                    if(atom.getType()==2) loose_c_atoms_found++;
                    else loose_e_atoms_found++;
                }
            }
        }
        if(Math.min(loose_c_atoms_found,loose_e_atoms_found)>0) return getError(3);
        return null; }
}

//*******************************************************************

class Line_Cs extends Level //3
{
    private Atom seed;

    public Line_Cs(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
                if(atom.getBonds().size()!=0) return getError(1);
                continue; // no other tests for non-'c' atoms
            }
            if(atom.getBonds().size()==1) single_bonded_atoms_found++;
            else if(atom.getBonds().size()==2) double_bonded_atoms_found++;
            else if(atom.getBonds().size()==0) return getError(2);
            else return getError(3);
            if(!joined.contains(atom)) return getError(4);
        }
        // one final check on chain configuration
        if(single_bonded_atoms_found!=2) return getError(5);
        return null;
    }
}

//*******************************************************************

class Join_all extends Level //4
{

    public Join_all(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
        if(joined.size()!=atoms.length) return getError(1);
        return null;
    }
}

//*******************************************************************

class Connect_corners extends Level //5
{

    public Connect_corners(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
        if(!joined.contains(atoms[1])) return getError(1);
        return null;
    }
}

//*******************************************************************

class Abcdef_chains extends Level //6
{

    public Abcdef_chains(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
        if(num_abcdef_chains_found==0) return getError(1);
        else if(num_abcdef_chains_found==1) return getError(2);
        return null;
    }
}

//*******************************************************************

class Join_same extends Level //7
{

    public Join_same(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
                if(other.getType() != atom.getType()) return getError(1);
            }
            // are there any atoms of the same type not on this list?
            for(int j=0;j<atoms.length;j++)
                if(atoms[j].getType() == atom.getType() && !joined.contains(atoms[j]))
                    return getError(2);
        }
        return null;
    }
}

//*******************************************************************

class Match_template extends Level //8
{

    public Match_template(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
            if(b.getType()!=a.getType() || b.getBonds().size()!=1) return getError(1);
            // (not a complete test, but hopefully ok)
        }
        return null;
    }
}

//*******************************************************************

class Break_molecule extends Level //9
{

    public Break_molecule(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
            if(atoms[i].getBonds().size() != n_bonds[i]) return getError(1);
        return null;
    }
}

//*******************************************************************

class Bond_prisoner extends Level //10
{
    private Atom prisoner;

    public Bond_prisoner(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
        if(prisoner.getBonds().size()==0) return getError(1);
        if(((Atom)atoms[8].getBonds().getFirst()).getType()!=5) return getError(2);
        return null; }
}

//*******************************************************************

class Pass_message extends Level //11
{

    public Pass_message(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
            if(atoms[i].getBonds().size() != n_bonds[i]) return getError(1);
            else if(atoms[i].getState() != 2) return getError(2);
        return null;
    }
}

//*******************************************************************

class Split_ladder extends Level //12
{

    public Split_ladder(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
            if(atoms[i].getBonds().size() != n_bonds[i%10]) return getError(1);
        return null; }
}

//*******************************************************************

class Insert_atom extends Level //13
{

    public Insert_atom(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
        if(joined.size()!=11) return getError(1);
        int n_bonds[]={1,2,2,2,2,2,2,2,2,2,1};
        int types[]={4,4,4,4,4,1,4,4,4,4,4};
        int i = 0;
        Iterator it = joined.iterator();
        while(it.hasNext()) {
            Atom a = (Atom)it.next();
            if(a.getBonds().size()!=n_bonds[i]) return getError(2);
            if( a.getType()!=types[i]) return getError(3);
            i++;
        }
        return null; }
}

//*******************************************************************

class Make_ladder extends Level //14
{

    public Make_ladder(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
        if(joined.size()>12) return getError(1);
        else if(joined.size()<12) return getError(2);
        // are the types matching?
        int original_type_count[] = {0,0,0,0,0,0},new_type_count[]={0,0,0,0,0,0};
        for(int i=0;i<6;i++) original_type_count[atoms[i].getType()]++;
        Iterator it = joined.iterator();
        while(it.hasNext()) new_type_count[((Atom)it.next()).getType()]++;
        for(int i=0;i<6;i++)
            if(new_type_count[i] != original_type_count[i]*2) 
                return getError(3);
        it = joined.iterator();
        while(it.hasNext()) { 
            Atom a = (Atom)it.next();
            if(a.getType()==4 || a.getType()==5) { 
                // 'e' and 'f' 
                if(a.getBonds().size()!=2) return getError(4);
            } else {
                if(a.getBonds().size()!=3) return getError(5);
            }
        }
        return null; }
}

//*******************************************************************

class Selfrep extends Level //15
{

    public Selfrep(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
                        return getError(1);

                    Atom last = (Atom)joined.getLast();
                    if (first.getBonds().size()!=1 || last.getBonds().size()!=1 || last.getType()!=5)
                        return getError(2);

                    for(int j=1;j<joined.size()-1;j++) {
                        Atom a = (Atom)joined.get(j);
                        if (a.getBonds().size()!=2)
                            return getError(3);			
                        if(a.getType() != atoms[j].getType())
                            return getError(4);
                    }
                    n_found++;
                }
            }
        }

        if(n_found==0)
            return getError(5);
        if(n_found<2)
            return getError(6);
        if (bound_atoms!=6*n_found)
            return getError(7);
        return null; }
}

//*******************************************************************

class Grow_membrane extends Level //16
{

    public Grow_membrane(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
                return getError(1);
            x_points[i] = (int)a.getPhysicalPoint().getPositionX(); // (need these for polygon check, below)
            y_points[i] = (int)a.getPhysicalPoint().getPositionY();
            i++;
        }
        // inside the polygon formed by the a atoms there should be exactly one atom - the original f1 (although its state may have changed)
        Atom f1 = atoms[8]; // see the setup code for this level
        Polygon poly = new Polygon(x_points,y_points,joined.size());
        if(!poly.contains(new Point2D.Float(f1.getPhysicalPoint().getPositionX(),f1.getPhysicalPoint().getPositionY())))
            return getError(2);
        // and no other 'a' atoms around
        for(i=0;i<atoms.length;i++) {
            Atom a = atoms[i];
            if(!joined.contains(a) && a.getType()==0)
                return getError(3);
        }
        return null; }
}

//*******************************************************************

class Membrane_transport extends Level //17
{

    public Membrane_transport(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
                return getError(1);
            x_points[i] = (int)a.getPhysicalPoint().getPositionX(); // (need these for polygon check, below)
            y_points[i] = (int)a.getPhysicalPoint().getPositionY();
            i++;
        }
        // inside should be the original 'b' atom, and all the 'f' atoms, and nothing else
        Atom b1 = atoms[12]; // see the setup code for this level
        Polygon poly = new Polygon(x_points,y_points,joined.size());
        if(!poly.contains(new Point2D.Float(b1.getPhysicalPoint().getPositionX(),b1.getPhysicalPoint().getPositionY())))
            return getError(2);
        // check the other atoms (want: f's inside, other's outside)
        for(i=joined.size()+1;i<atoms.length;i++) {
            Atom a = atoms[i];
            if(a.getType()==5 && !poly.contains(new Point2D.Float(a.getPhysicalPoint().getPositionX(),a.getPhysicalPoint().getPositionY())))
                return getError(3);
            else if(a.getType()!=5 && poly.contains(new Point2D.Float(a.getPhysicalPoint().getPositionX(),a.getPhysicalPoint().getPositionY())))
                return getError(4);
        }
        return null; }
}

//*******************************************************************

class Membrane_division extends Level //18
{

    public Membrane_division(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
            return getError(1);
        // and there should be a second loop of 'a' atoms made of the same atoms
        for(int i=0;i<N;i++) {
            Atom a = atoms[i];
            if(!loop[0].contains(a)) a.getAllConnectedAtoms(loop[1]);
        }
        if(loop[0].size()+loop[1].size()!=N)
            return getError(2);
        // each atom in each group should of type 'a' and have exactly two bonds (hence a neat loop)
        for(int iLoop=0;iLoop<2;iLoop++) {
            for(int i=0;i<loop[iLoop].size();i++) {
                Atom a = (Atom)loop[iLoop].get(i);
                if(a.getType()!=0 || a.getBonds().size()!=2)
                    return getError(3);
            }
        }
        return null; }
}

//*******************************************************************

class Cell_division extends Level //19
{

    public Cell_division(String title, String challenge, String hint, String[] errors,
            Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
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
            return getError(1);
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
                        return getError(2);
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
            return getError(3);
        // each head should be in a separate component
        LinkedList c1 = (LinkedList)components.get(0),c2=(LinkedList)components.get(1);
        if( (c1.contains(heads[0]) && !c2.contains(heads[1])) || (c2.contains(heads[0]) && !c1.contains(heads[1])) )
            return getError(4);
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
                return getError(5);
        }
        if(sequence[0].compareTo(sequence[1])!=0) return getError(6);

        return null;
    }
}
