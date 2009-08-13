package uk.org.squirm3.engine;

import uk.org.squirm3.data.Level;

import java.util.List;

/**
 * ${my.copyright}
 */

public final class LevelManager {
    private Level currentLevel;
    private final List<Level> levels;
    private int levelIndex;

    public LevelManager(List<Level> levels) {
        this.levels = levels;
        levelIndex = 0;
        setLevel(0);
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentLevelIndex() {
        return levelIndex;
    }

    public List<? extends Level> getLevels() {
        return levels;
    }

    public int getNumberOfLevel() {
        return levels.size();
    }

    protected void setLevel(int index) {
        // TODO exception if index out of bounds
        // TODO why protected ?
        if (index > levels.size()) index = levels.size() - 1;
        if (index < 0) return;
        levelIndex = index;
        currentLevel = levels.get(index);
    }
}
