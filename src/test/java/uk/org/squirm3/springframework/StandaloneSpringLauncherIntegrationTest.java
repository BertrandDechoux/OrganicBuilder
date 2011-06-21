package uk.org.squirm3.springframework;

import org.junit.Test;

public class StandaloneSpringLauncherIntegrationTest {

    @Test
    public void shouldLoadApplicationContextWithoutException() {
        StandaloneSpringLauncher.main();
    }

}
