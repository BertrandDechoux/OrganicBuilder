package uk.org.squirm3.config;

import java.awt.Image;

import javax.swing.ImageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;

import uk.org.squirm3.config.gui.ActionConfigurer;
import uk.org.squirm3.config.gui.ActionConfigurerFactory;
import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.ui.GUI;
import uk.org.squirm3.ui.collider.AtomsPanel;
import uk.org.squirm3.ui.level.CurrentLevelPanel;
import uk.org.squirm3.ui.reaction.ReactionConstructorPanel;
import uk.org.squirm3.ui.reaction.ReactionListPanel;
import uk.org.squirm3.ui.toolbar.AboutAction;
import uk.org.squirm3.ui.toolbar.ToolBarPanel;
import uk.org.squirm3.ui.toolbar.navigation.FirstLevelAction;
import uk.org.squirm3.ui.toolbar.navigation.LastLevelAction;
import uk.org.squirm3.ui.toolbar.navigation.LevelPicker;
import uk.org.squirm3.ui.toolbar.navigation.NextLevelAction;
import uk.org.squirm3.ui.toolbar.navigation.PreviousLevelAction;
import uk.org.squirm3.ui.toolbar.simulation.ResetSimulationAction;
import uk.org.squirm3.ui.toolbar.simulation.RunSimulationAction;
import uk.org.squirm3.ui.toolbar.simulation.SpeedPanel;
import uk.org.squirm3.ui.toolbar.simulation.StopSimulationAction;

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
    public GUI getGUI(MessageSource messageSource, CurrentLevelPanel currentLevelPanel, ReactionListPanel reactionListPanel, ReactionConstructorPanel reactionConstructorPanel,
            AtomsPanel collisionsPanel, ToolBarPanel toolBarPanel) {
        return new GUI(messageSource, currentLevelPanel, reactionListPanel, reactionConstructorPanel, collisionsPanel, toolBarPanel);
    }

    @Bean
    public ToolBarPanel getToolBarPanel(RunSimulationAction runSimulationAction, StopSimulationAction stopSimulationAction, ResetSimulationAction resetSimulationAction, SpeedPanel speedPanel,
            FirstLevelAction firstLevelAction, PreviousLevelAction previousLevelAction, LevelPicker levelPicker, NextLevelAction nextLevelAction, LastLevelAction lastLevelAction,
            AboutAction aboutAction) {
        return new ToolBarPanel(runSimulationAction, stopSimulationAction, resetSimulationAction, speedPanel, firstLevelAction, previousLevelAction, levelPicker, nextLevelAction, lastLevelAction,
                aboutAction);
    }

    @Bean
    public SpeedPanel getSpeedPanel() {
        return new SpeedPanel(this.applicationEngine, this.messageSource);
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
    public ActionConfigurer getActionConfigurer() {
        return ActionConfigurerFactory.createDefaultConfigurer(this.environment, this.messageSource, this.conversionService);
    }

    @Bean
    public RunSimulationAction getRunSimulationActionConfiguration(ActionConfigurer configurer) {
        return configurer.configure(new RunSimulationAction(this.applicationEngine), "simulation.run");
    }

    @Bean
    public StopSimulationAction getStopSimulationAction(ActionConfigurer configurer) {
        return configurer.configure(new StopSimulationAction(this.applicationEngine), "simulation.stop");
    }

    @Bean
    public ResetSimulationAction getResetSimulationAction(ActionConfigurer configurer) {
        return configurer.configure(new ResetSimulationAction(this.applicationEngine), "simulation.reset");
    }

    @Bean
    public FirstLevelAction getFirstLevelAction(ActionConfigurer configurer) {
        return configurer.configure(new FirstLevelAction(this.applicationEngine), "level.first");
    }

    @Bean
    public PreviousLevelAction getPreviousLevelAction(ActionConfigurer configurer) {
        return configurer.configure(new PreviousLevelAction(this.applicationEngine), "level.previous");
    }

    @Bean
    public NextLevelAction getNextLevelAction(ActionConfigurer configurer) {
        return configurer.configure(new NextLevelAction(this.applicationEngine), "level.next");
    }

    @Bean
    public LastLevelAction getLastLevelAction(ActionConfigurer configurer) {
        return configurer.configure(new LastLevelAction(this.applicationEngine), "level.last");
    }

    @Bean
    public AboutAction getAboutAction(@Value("${about.url}") String aboutUrl, ActionConfigurer configurer) {
        return configurer.configure(new AboutAction(aboutUrl, this.messageSource), "about");
    }

    @Bean
    public AtomsPanel getAtomsPanel(@Value("/graphics/spiky.png") Image spikyImage) {
        return new AtomsPanel(this.applicationEngine, spikyImage);
    }

    @Bean
    public ReactionConstructorPanel getReactionConstructorPanel(@Value("/graphics/add.png") ImageIcon addIcon) {
        return new ReactionConstructorPanel(this.applicationEngine, this.messageSource, addIcon);
    }
}
