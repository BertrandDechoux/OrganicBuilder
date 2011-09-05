package uk.org.squirm3.model.level;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class LevelsIntegrationTest {
    private final Level level;

    public LevelsIntegrationTest(final Level level) {
        this.level = level;
    }

    @Parameters
    public static Collection<Object[]> parameters() {
        final ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "engine-context.xml");

        @SuppressWarnings("unchecked")
        final List<Level> levels = applicationContext.getBean("levels",
                List.class);
        final Collection<Object[]> parameters = Lists.newArrayList();
        for (final Level level : levels) {
            parameters.add(new Object[]{level});
        }
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
