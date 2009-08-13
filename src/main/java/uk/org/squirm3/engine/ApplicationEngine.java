package uk.org.squirm3.engine;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;

/**
 * ${my.copyright}
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
        // load levels
        levelManager = new LevelManager(Application.getLevels());
        reactionManager = new ReactionManager();
        // manager of the listeners
        engineDispatcher = new EngineDispatcher();
        sleepPeriod = 50;
        // start the challenge by the introduction
        try {
            setLevel(0, null);
        } catch (Exception e) {
        }
        commands = new LinkedList();
        colliderExecution = new Object();
        isRunning = true;
        // create and run the thread of this application
        applicationThread = new Thread(
                new Runnable() {
                    public void run() {
                        while (applicationThread == Thread.currentThread()) {
                            // execute commands
                            synchronized (commands) {
                                while (!commands.isEmpty()) {
                                    ICommand command = (ICommand) commands.removeFirst();
                                    command.execute();
                                }
                            }
                            // compute one step of the simulation
                            synchronized (colliderExecution) {
                                if (isRunning) {
                                    lastUsedDraggingPoint = draggingPoint;
                                    collider.doTimeStep(draggingPoint, new LinkedList(reactionManager.getReactions()));
                                    engineDispatcher.atomsHaveChanged();
                                    try {
                                        Thread.sleep(sleepPeriod);
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                } else {
                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
                                        break;
                                    }
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
                if (!reactionManager.getReactions().isEmpty()) {
                    reactionManager.clearReactions();
                    engineDispatcher.reactionsHaveChanged();
                }
            }
        });
    }

    //TODO avoid to create a copy per call
    public Collection getAtoms() {
        synchronized (colliderExecution) {
            Atom[] atoms = collider.getAtoms();
            List list = new LinkedList();
            for (int i = 0; i < atoms.length; i++) {
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
                synchronized (colliderExecution) {
                    if (isRunning) {
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
                if (atoms == null) return;
                collider = new Collider(atoms, (int) currentLevel.getConfiguration().getWidth(),
                        (int) currentLevel.getConfiguration().getHeight());
                if (configuration != null) engineDispatcher.configurationHasChanged();
                engineDispatcher.atomsHaveChanged();
                synchronized (colliderExecution) {
                    if (!isRunning) {
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
                synchronized (colliderExecution) {
                    if (!isRunning) {
                        isRunning = true;
                        engineDispatcher.simulationStateHasChanged();
                    }
                }
            }
        });
    }

    public void setDraggingPoint(DraggingPoint newDraggingPoint) {
        if (draggingPoint == null && newDraggingPoint == null) return;
        if (draggingPoint == null && newDraggingPoint != null
                || draggingPoint != null && newDraggingPoint == null) {
            draggingPoint = newDraggingPoint;
            engineDispatcher.draggingPointHasChanged();
            return;
        }
        if (draggingPoint.equals(newDraggingPoint)) return;
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
        if (collider != null) {
            reactionManager.clearReactions();
            engineDispatcher.reactionsHaveChanged();
        }
        Level currentLevel = levelManager.getCurrentLevel();
        Atom[] atoms = currentLevel.createAtoms(configuration);
        if (atoms == null) return;
        collider = new Collider(atoms, (int) currentLevel.getConfiguration().getWidth(),
                (int) currentLevel.getConfiguration().getHeight());
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

    public void goToFirstLevel() {
        goToLevel(0, null);
    }

    public void goToLastLevel() {
        addCommand(new ICommand() {
            public void execute() {
                setLevel(levelManager.getNumberOfLevel() - 1, null);
            }
        });
    }

    public void goToNextLevel() {
        addCommand(new ICommand() {
            public void execute() {
                int levelIndex = levelManager.getCurrentLevelIndex();
                if (levelIndex + 1 < levelManager.getNumberOfLevel()) setLevel(levelIndex + 1, null);
            }
        });
    }

    public void goToPreviousLevel() {
        addCommand(new ICommand() {
            public void execute() {
                int levelIndex = levelManager.getCurrentLevelIndex();
                if (levelIndex - 1 >= 0) setLevel(levelIndex - 1, null);
            }
        });
    }

    private interface ICommand {
        public void execute();
    }

    private void addCommand(ICommand c) {
        synchronized (commands) {
            commands.add(c);
        }
    }

}
