package uk.org.squirm3.engine;

import java.util.Collection;
import java.util.LinkedList;

import uk.org.squirm3.listener.EventDispatcher;
import uk.org.squirm3.listener.Listener;
import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.DraggingPoint;
import uk.org.squirm3.model.Reaction;
import uk.org.squirm3.model.level.Level;

public class ApplicationEngine {

    // things to run the collider and the commands
    private Collider collider;
    private final Thread applicationThread;
    private final Object colliderExecution;
    private boolean isRunning;
    private short sleepPeriod;
    private final LinkedList<ICommand> commands;
    // things to do with the dragging around of atoms
    private DraggingPoint draggingPoint;
    private DraggingPoint lastUsedDraggingPoint;
    // composition
    private final LevelManager levelManager;
    private final ReactionManager reactionManager;

    private final EventDispatcher<ApplicationEngineEvent> eventDispatcher;
    private Configuration configuration;

    public ApplicationEngine(final LevelManager levelManager) throws Exception {
        // load levels
        this.levelManager = levelManager;
        reactionManager = new ReactionManager();
        // manager of the listeners
        eventDispatcher = new EventDispatcher<ApplicationEngineEvent>();
        sleepPeriod = 50;
        // start the challenge by the introduction
        try {
            setLevel(0);
        } catch (final Exception e) {
        }
        commands = new LinkedList<ICommand>();
        colliderExecution = new Object();
        isRunning = true;
        // create and run the thread of this application
        applicationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (applicationThread == Thread.currentThread()) {
                    // execute commands
                    synchronized (commands) {
                        while (!commands.isEmpty()) {
                            final ICommand command = commands.removeFirst();
                            command.execute();
                        }
                    }
                    // compute one step of the simulation
                    synchronized (colliderExecution) {
                        if (isRunning) {
                            lastUsedDraggingPoint = draggingPoint;
                            collider.doTimeStep(
                                    draggingPoint,
                                    new LinkedList<Reaction>(reactionManager
                                            .getReactions()));
                            eventDispatcher
                                    .dispatchEvent(ApplicationEngineEvent.ATOMS);
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

    public Configuration getConfiguration() {
        return configuration;
    }

    public void clearReactions() {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                if (!reactionManager.getReactions().isEmpty()) {
                    reactionManager.clearReactions();
                    eventDispatcher
                            .dispatchEvent(ApplicationEngineEvent.REACTIONS);
                }
            }
        });
    }

    public Collection<? extends Atom> getAtoms() {
        // TODO synchronize correctly : atoms should not be iterated while the
        // collider is working on theù
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

    public Collection<Reaction> getReactions() {
        return reactionManager.getReactions();
    }

    public short getSimulationSpeed() {
        return sleepPeriod;
    }

    public void pauseSimulation() {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                synchronized (colliderExecution) {
                    if (isRunning) {
                        isRunning = false;
                        eventDispatcher
                                .dispatchEvent(ApplicationEngineEvent.SIMULATION_STATE);
                    }
                }
            }
        });
    }

    public void addReactions(final Collection<Reaction> reactions) {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                reactionManager.addReactions(reactions);
                eventDispatcher.dispatchEvent(ApplicationEngineEvent.REACTIONS);
            }
        });
    }

    public void removeReactions(final Collection<Reaction> reactions) {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                reactionManager.removeReactions(reactions);
                eventDispatcher.dispatchEvent(ApplicationEngineEvent.REACTIONS);
            }
        });
    }

    private void setLevel(final int levelIndex) {
        levelManager.setLevel(levelIndex);
        if (collider != null) {
            reactionManager.clearReactions();
            eventDispatcher.dispatchEvent(ApplicationEngineEvent.REACTIONS);
        }
        loadCurrentLevel();
        eventDispatcher.dispatchEvent(ApplicationEngineEvent.LEVEL);
        return;
    }

    private void loadCurrentLevel() {
        final Level currentLevel = levelManager.getCurrentLevel();
        configuration = currentLevel.construct();
        collider = new Collider(configuration);
        eventDispatcher.dispatchEvent(ApplicationEngineEvent.CONFIGURATION);
        eventDispatcher.dispatchEvent(ApplicationEngineEvent.ATOMS);
    }

    public void restartLevel() {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                loadCurrentLevel();
                synchronized (colliderExecution) {
                    if (!isRunning) {
                        isRunning = true;
                        eventDispatcher
                                .dispatchEvent(ApplicationEngineEvent.SIMULATION_STATE);
                    }
                }
            }
        });
    }

    public void runSimulation() {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                synchronized (colliderExecution) {
                    if (!isRunning) {
                        isRunning = true;
                        eventDispatcher
                                .dispatchEvent(ApplicationEngineEvent.SIMULATION_STATE);
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
            eventDispatcher
                    .dispatchEvent(ApplicationEngineEvent.DRAGGING_POINT);
            return;
        }
        if (draggingPoint.equals(newDraggingPoint)) {
            return;
        }
        draggingPoint = newDraggingPoint;
        eventDispatcher.dispatchEvent(ApplicationEngineEvent.DRAGGING_POINT);
    }

    public void setReactions(final Collection<Reaction> reactions) {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                reactionManager.clearReactions();
                reactionManager.addReactions(reactions);
                eventDispatcher.dispatchEvent(ApplicationEngineEvent.REACTIONS);
            }
        });
    }

    public void setSimulationSpeed(final short newSleepPeriod) {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                sleepPeriod = newSleepPeriod;
                eventDispatcher.dispatchEvent(ApplicationEngineEvent.SPEED);
            }
        });
    }

    public boolean simulationIsRunning() {
        return isRunning;
    }

    public final void addListener(final Listener listener,
            final ApplicationEngineEvent event) {
        listener.propertyHasChanged();
        eventDispatcher.addListener(listener, event);
    }

    public void goToLevel(final int levelIndex) {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                setLevel(levelIndex);
            }
        });
    }

    public void goToFirstLevel() {
        goToLevel(0);
    }

    public void goToLastLevel() {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                setLevel(levelManager.getNumberOfLevel() - 1);
            }
        });
    }

    public void goToNextLevel() {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                final int levelIndex = levelManager.getCurrentLevelIndex();
                if (levelIndex + 1 < levelManager.getNumberOfLevel()) {
                    setLevel(levelIndex + 1);
                }
            }
        });
    }

    public void goToPreviousLevel() {
        addCommand(new ICommand() {
            @Override
            public void execute() {
                final int levelIndex = levelManager.getCurrentLevelIndex();
                if (levelIndex - 1 >= 0) {
                    setLevel(levelIndex - 1);
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
