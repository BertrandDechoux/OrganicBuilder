package uk.org.squirm3.engine;

import java.util.List;

import uk.org.squirm3.model.level.ILevel;

public final class LevelManager {
    private ILevel currentLevel;
    private final List<ILevel> levels;
    private int levelIndex;

    public LevelManager(final List<ILevel> levels) {
        this.levels = levels;
        levelIndex = 0;
        setLevel(0);
    }

    public ILevel getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentLevelIndex() {
        return levelIndex;
    }

    public List<? extends ILevel> getLevels() {
        return levels;
    }

    public int getNumberOfLevel() {
        return levels.size();
    }

    protected void setLevel(int index) {
        // TODO exception if index out of bounds
        // TODO why protected ?
        if (index > levels.size()) {
            index = levels.size() - 1;
        }
        if (index < 0) {
            return;
        }
        levelIndex = index;
        currentLevel = levels.get(index);
    }
}
