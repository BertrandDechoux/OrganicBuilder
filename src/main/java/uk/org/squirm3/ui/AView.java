package uk.org.squirm3.ui;

import uk.org.squirm3.engine.ApplicationEngine;

/**
 * ${my.copyright}
 */

public abstract class AView {

    private final ApplicationEngine applicationEngine;

    public AView(ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
    }

    public ApplicationEngine getApplicationEngine() {
        return applicationEngine;
    }
}
