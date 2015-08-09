package uk.org.squirm3.config;

import java.awt.Image;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.ui.GUI;
import uk.org.squirm3.ui.collider.AtomsPanel;
import uk.org.squirm3.ui.level.CurrentLevelPanel;
import uk.org.squirm3.ui.reaction.ReactionConstructorPanel;
import uk.org.squirm3.ui.reaction.ReactionListPanel;
import uk.org.squirm3.ui.toolbar.AboutButton;
import uk.org.squirm3.ui.toolbar.ToolBarPanel;
import uk.org.squirm3.ui.toolbar.navigation.FirstLevelButton;
import uk.org.squirm3.ui.toolbar.navigation.LastLevelButton;
import uk.org.squirm3.ui.toolbar.navigation.LevelPicker;
import uk.org.squirm3.ui.toolbar.navigation.NextLevelButton;
import uk.org.squirm3.ui.toolbar.navigation.PreviousLevelAction;
import uk.org.squirm3.ui.toolbar.simulation.ResetSimulationButton;
import uk.org.squirm3.ui.toolbar.simulation.RunSimulationButton;
import uk.org.squirm3.ui.toolbar.simulation.SpeedPane;
import uk.org.squirm3.ui.toolbar.simulation.StopSimulationButton;

@Configuration
public class GuiConfig {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ApplicationEngine applicationEngine;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private Environment environment;

    @Bean
    public GUI getGUI(Stage primaryStage, MessageSource messageSource, CurrentLevelPanel currentLevelPanel, ReactionListPanel reactionListPanel, ReactionConstructorPanel reactionConstructorPanel,
            AtomsPanel collisionsPanel, ToolBarPanel toolBarPanel) {
        return new GUI(primaryStage, messageSource, currentLevelPanel, reactionListPanel, reactionConstructorPanel, collisionsPanel, toolBarPanel);
    }

    @Bean
    public ToolBarPanel getToolBarPanel(RunSimulationButton runSimulationAction, StopSimulationButton stopSimulationAction, ResetSimulationButton resetSimulationAction, SpeedPane speedPanel,
            FirstLevelButton firstLevelAction, PreviousLevelAction previousLevelAction, LevelPicker levelPicker, NextLevelButton nextLevelAction, LastLevelButton lastLevelAction,
            AboutButton aboutAction) {
        return new ToolBarPanel(runSimulationAction, stopSimulationAction, resetSimulationAction, speedPanel, firstLevelAction, previousLevelAction, levelPicker, nextLevelAction, lastLevelAction,
                aboutAction);
    }

    @Bean
    public SpeedPane getSpeedPanel() {
        return new SpeedPane(this.applicationEngine, this.messageSource);
    }

    @Bean
    public LevelPicker getLevelPicker() {
        return new LevelPicker(this.applicationEngine, this.messageSource);
    }

    @Bean
    public CurrentLevelPanel getCurrentLevelPanel() {
        return new CurrentLevelPanel(this.applicationEngine, this.messageSource);
    }

    @Bean
    public ReactionListPanel getReactionListPanel() {
        return new ReactionListPanel(this.applicationEngine, this.messageSource, this.conversionService);
    }

    @Bean
    public RunSimulationButton getRunSimulationActionConfiguration() {
        return configure(new RunSimulationButton(this.applicationEngine), "simulation.run");
    }

    @Bean
    public StopSimulationButton getStopSimulationButton() {
    	return configure(new StopSimulationButton(this.applicationEngine), "simulation.stop");
    }

    @Bean
    public ResetSimulationButton getResetSimulationButton() {
    	return configure(new ResetSimulationButton(this.applicationEngine), "simulation.reset");
    }

    @Bean
    public FirstLevelButton getFirstLevelButton() {
    	return configure(new FirstLevelButton(this.applicationEngine), "level.first");
    }

    @Bean
    public PreviousLevelAction getPreviousLevelButton() {
    	return configure(new PreviousLevelAction(this.applicationEngine), "level.previous");
    }

    @Bean
    public NextLevelButton getNextLevelButton() {
    	return configure(new NextLevelButton(this.applicationEngine), "level.next");
    }

    @Bean
    public LastLevelButton getLastLevelButton() {
    	return configure(new LastLevelButton(this.applicationEngine), "level.last");
    }

    @Bean
    public AboutButton getAboutButton(@Value("${about.url}") String aboutUrl) {
    	return configure(new AboutButton(aboutUrl, this.messageSource), "about");
    }

    @Bean
    public AtomsPanel getAtomsPanel(@Value("/graphics/spiky.png") Image spikyImage) {
        return new AtomsPanel(this.applicationEngine, spikyImage);
    }

    @Bean
    public ReactionConstructorPanel getReactionConstructorPanel() {
    	javafx.scene.image.Image addIcon = new javafx.scene.image.Image(getClass().getResourceAsStream("/graphics/add.png"));
        return new ReactionConstructorPanel(this.applicationEngine, this.messageSource, addIcon);
    }
    
    private <T extends Button> T configure(final T button, final String identifier) {
		String text = getMessage(identifier + ".button.tooltip");
		button.setTooltip(new Tooltip(text));

		String path = getMessage(identifier + ".button.icon");
		javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResourceAsStream(path));
		button.setGraphic(new ImageView(image));

		return button;
    }

    /**
     * Search properties and if not found messages.
     */
    private String getMessage(final String messageCode) {
        final String messageFromProperty = this.environment.getProperty(messageCode);
        if (messageFromProperty != null) {
            return messageFromProperty;
        }

        return this.messageSource.getMessage(messageCode, null, null, Locale.getDefault());
    }
}
