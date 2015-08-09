

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.org.squirm3.config.EngineConfig;
import uk.org.squirm3.config.GuiConfig;

/**
 * Startup for standalone application.
 */
public class OrganicBuilder extends Application {
    /**
     * @param args unused arguments from cli
     */
    public static void main(final String... args) {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.getBeanFactory().registerSingleton("primaryStage", primaryStage);
		context.register(EngineConfig.class, GuiConfig.class);
		context.refresh();
		context.close();
	}

}
