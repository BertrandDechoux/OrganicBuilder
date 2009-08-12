package uk.org.squirm3.engine;

import uk.org.squirm3.data.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * ${my.copyright}
 */

public final class LevelManager {
    private Level currentLevel;
    private final List levelList;
    private int levelIndex;

    public LevelManager() {
        levelList = new ArrayList();
        levelIndex = -1;
        currentLevel = null;
    }

    protected void addLevel(Level l) {
        levelList.add(l);
        if (levelIndex == -1) {
            levelIndex = 0;
            currentLevel = l;
        }
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentLevelIndex() {
        return levelIndex;
    }

    public List getLevels() {
        return new ArrayList(levelList);
    }

    public int getNumberOfLevel() {
        return levelList.size();
    }

    protected void setLevel(int index) {
        if (index > levelList.size()) index = levelList.size() - 1;
        if (index < 0) return;
        levelIndex = index;
        currentLevel = (Level) levelList.get(index);
    }
}
