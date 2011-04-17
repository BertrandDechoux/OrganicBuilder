package uk.org.squirm3.engine;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.DraggingPoint;
import uk.org.squirm3.data.ILevel;
import uk.org.squirm3.listener.EventDispatcher;

public class ApplicationEngine {

    // things to run the collider and the commands
    private Collider collider;
    private final Thread applicationThread;
    private final Object colliderExecution;
    private boolean isRunning;
    private short sleepPeriod;
    private final LinkedList commands;
    // things to do with the dragging around of atoms
    private DraggingPoint draggingPoint;
    private DraggingPoint lastUsedDraggingPoint;
    // composition
    private final LevelManager levelManager;
    private final ReactionManager reactionManager;

    private final EventDispatcher eventDispatcher;

    public ApplicationEngine() throws Exception {
        // load levels
        levelManager = new LevelManager(Application.getLevels());
        reactionManager = new ReactionManager();
        // manager of the listeners
        eventDispatcher = new EventDispatcher();
        sleepPeriod = 50;
        // start the challenge by the introduction
        try {
            setLevel(0, null);
        } catch (final Exception e) {
        }
        commands = new LinkedList();
        colliderExecution = new Object();
        isRunning = true;
        // create and run the thread of this application
        applicationThread = new Thread(new Runnable() {
            public void run() {
                while (applicationThread == Thread.currentThread()) {
                    // execute commands
                    synchronized (commands) {
                        while (!commands.isEmpty()) {
                            final ICommand command = (ICommand) commands
                                    .removeFirst();
                            command.execute();
                        }
                    }
                    // compute one step of the simulation
                    synchronized (colliderExecution) {
                        if (isRunning) {
                            lastUsedDraggingPoint = draggingPoint;
                            collider.doTimeStep(draggingPoint, new LinkedList(
                                    reactionManager.getReactions()));
                            eventDispatcher
                                    .dispatchEvent(EventDispatcher.Event.ATOMS);
                            try {
                                Thread.sleep(sleepPeriod);
                            } catch (final InterruptedException e) {
                                break;
                            }
                        } else {
                            try {
                                Thread.sleep(5);
                            } catch (final InterruptedException e) {
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
                    eventDispatcher
                            .dispatchEvent(EventDispatcher.Event.REACTIONS);
                }
            }
        });
    }

    public Collection getAtoms() {
        synchronized (colliderExecution) {
            return collider.getAtoms();
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
                        eventDispatcher
                                .dispatchEvent(EventDispatcher.Event.SIMULATION_STATE);
                    }
                }
            }
        });
    }

    public void addReactions(final Collection reactions) {
        addCommand(new ICommand() {
            public void execute() {
                reactionManager.addReactions(reactions);
                eventDispatcher.dispatchEvent(EventDispatcher.Event.REACTIONS);
            }
        });
    }

    public void removeReactions(final Collection reactions) {
        addCommand(new ICommand() {
            public void execute() {
                reactionManager.removeReactions(reactions);
                eventDispatcher.dispatchEvent(EventDispatcher.Event.REACTIONS);
            }
        });
    }

    public void restartLevel(final Configuration configuration) {
        addCommand(new ICommand() {
            public void execute() {
                final ILevel currentLevel = levelManager.getCurrentLevel();
                final List<Atom> atoms = currentLevel
                        .generateAtoms(configuration);
                if (atoms == null) {
                    return;
                }
                collider = new Collider(atoms, (int) currentLevel
                        .getConfiguration().getWidth(), (int) currentLevel
                        .getConfiguration().getHeight());
                if (configuration != null) {
                    eventDispatcher
                            .dispatchEvent(EventDispatcher.Event.CONFIGURATION);
                }
                eventDispatcher.dispatchEvent(EventDispatcher.Event.ATOMS);
                synchronized (colliderExecution) {
                    if (!isRunning) {
                        isRunning = true;
                        eventDispatcher
                                .dispatchEvent(EventDispatcher.Event.SIMULATION_STATE);
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
                        eventDispatcher
                                .dispatchEvent(EventDispatcher.Event.SIMULATION_STATE);
                    }
                }
            }
        });
    }

    public void setDraggingPoint(final DraggingPoint newDraggingPoint) {
        if (draggingPoint == null && newDraggingPoint == null) {
            return;
        }
        if (draggingPoint == null && newDraggingPoint != null
                || draggingPoint != null && newDraggingPoint == null) {
            draggingPoint = newDraggingPoint;
            eventDispatcher.dispatchEvent(EventDispatcher.Event.DRAGGING_POINT);
            return;
        }
        if (draggingPoint.equals(newDraggingPoint)) {
            return;
        }
        draggingPoint = newDraggingPoint;
        eventDispatcher.dispatchEvent(EventDispatcher.Event.DRAGGING_POINT);
    }

    public void setReactions(final Collection reactions) {
        addCommand(new ICommand() {
            public void execute() {
                reactionManager.clearReactions();
                reactionManager.addReactions(reactions);
                eventDispatcher.dispatchEvent(EventDispatcher.Event.REACTIONS);
            }
        });
    }

    public void setSimulationSpeed(final short newSleepPeriod) {
        addCommand(new ICommand() {
            public void execute() {
                sleepPeriod = newSleepPeriod;
                eventDispatcher.dispatchEvent(EventDispatcher.Event.SPEED);
            }
        });
    }

    public boolean simulationIsRunning() {
        return isRunning;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    private void setLevel(final int levelIndex,
            final Configuration configuration) {
        levelManager.setLevel(levelIndex);
        if (collider != null) {
            reactionManager.clearReactions();
            eventDispatcher.dispatchEvent(EventDispatcher.Event.REACTIONS);
        }
        final ILevel currentLevel = levelManager.getCurrentLevel();
        final List<Atom> atoms = currentLevel.generateAtoms(configuration);
        if (atoms == null) {
            return;
        }
        collider = new Collider(atoms, (int) currentLevel.getConfiguration()
                .getWidth(), (int) currentLevel.getConfiguration().getHeight());
        eventDispatcher.dispatchEvent(EventDispatcher.Event.ATOMS);
        eventDispatcher.dispatchEvent(EventDispatcher.Event.LEVEL);
        return;
    }

    public void goToLevel(final int levelIndex,
            final Configuration configuration) {
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
                final int levelIndex = levelManager.getCurrentLevelIndex();
                if (levelIndex + 1 < levelManager.getNumberOfLevel()) {
                    setLevel(levelIndex + 1, null);
                }
            }
        });
    }

    public void goToPreviousLevel() {
        addCommand(new ICommand() {
            public void execute() {
                final int levelIndex = levelManager.getCurrentLevelIndex();
                if (levelIndex - 1 >= 0) {
                    setLevel(levelIndex - 1, null);
                }
            }
        });
    }

    private interface ICommand {
        public void execute();
    }

    private void addCommand(final ICommand c) {
        synchronized (commands) {
            commands.add(c);
        }
    }

}
