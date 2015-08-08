package uk.org.squirm3.engine;

import java.util.List;

import uk.org.squirm3.model.level.Level;

public final class LevelManager {
    private Level currentLevel;
    private final List<Level> levels;
    private int levelIndex;

    public LevelManager(final List<Level> levels) {
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

    public boolean isCurrentLevelFirstLevel() {
        return levelIndex == 0;
    }

    public boolean isCurrentLevelLastLevel() {
        return levelIndex == levels.size() - 1;
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
