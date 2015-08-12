package uk.org.squirm3.model.level;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.collect.Lists;

import uk.org.squirm3.config.EngineConfig;
import uk.org.squirm3.engine.LevelManager;

@RunWith(Parameterized.class)
public class LevelsIntegrationTest {
    private final Level level;

    public LevelsIntegrationTest(final Level level) {
        this.level = level;
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(EngineConfig.class);

        final List<? extends Level> levels = applicationContext.getBean(LevelManager.class).getLevels();
        final Collection<Object[]> parameters = Lists.newArrayList();
        for (final Level level : levels) {
            parameters.add(new Object[] { level });
        }
        Assert.assertEquals(levels.size(), 21);
        applicationContext.close();
        return parameters;
    }

    @Test
    public void shouldConstructSucessfullyAllLevels() {
        level.construct();
        level.getTitle();
        level.getChallenge();
        level.getHint();
    }

}
