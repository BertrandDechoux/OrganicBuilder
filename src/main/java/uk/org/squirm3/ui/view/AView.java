package uk.org.squirm3.ui.view;

import uk.org.squirm3.engine.ApplicationEngine;

public abstract class AView {

    private final ApplicationEngine applicationEngine;

    public AView(final ApplicationEngine applicationEngine) {
        this.applicationEngine = applicationEngine;
    }

    public ApplicationEngine getApplicationEngine() {
        return applicationEngine;
    }
}
