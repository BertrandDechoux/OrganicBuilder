package uk.org.squirm3.config;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;

import uk.org.squirm3.engine.ApplicationEngine;
import uk.org.squirm3.engine.LevelManager;
import uk.org.squirm3.engine.generator.AtomBuilder;
import uk.org.squirm3.engine.generator.RandomConstructor;
import uk.org.squirm3.model.level.ComposedLevelFactory;
import uk.org.squirm3.model.level.Level;
import uk.org.squirm3.model.level.validators.AbcdefChainsValidator;
import uk.org.squirm3.model.level.validators.BondPrisonerValidator;
import uk.org.squirm3.model.level.validators.BreakMoleculeValidator;
import uk.org.squirm3.model.level.validators.CellDivisionValidator;
import uk.org.squirm3.model.level.validators.ConnectCornersValidator;
import uk.org.squirm3.model.level.validators.GrowMembraneValidator;
import uk.org.squirm3.model.level.validators.InsertAtomValidator;
import uk.org.squirm3.model.level.validators.IntroValidator;
import uk.org.squirm3.model.level.validators.JoinAllValidator;
import uk.org.squirm3.model.level.validators.JoinValidator;
import uk.org.squirm3.model.level.validators.JoinSameValidator;
import uk.org.squirm3.model.level.validators.LineCsValidator;
import uk.org.squirm3.model.level.validators.PairValidator;
import uk.org.squirm3.model.level.validators.MakeLadderValidator;
import uk.org.squirm3.model.level.validators.MatchTemplateValidator;
import uk.org.squirm3.model.level.validators.MembraneTransportValidator;
import uk.org.squirm3.model.level.validators.PassMessageValidator;
import uk.org.squirm3.model.level.validators.SelfrepValidator;
import uk.org.squirm3.model.level.validators.SplitLadderValidator;
import uk.org.squirm3.model.type.def.BasicType;

import com.google.common.collect.Lists;

@Configuration
@Import(SpringConfig.class)
public class EngineConfig {
    @Bean(name = "applicationEngine")
    public ApplicationEngine getApplicationEngine(LevelManager levelManager) throws Exception {
        return new ApplicationEngine(levelManager);
    }

    @Bean
    public LevelManager getLevelManager(ComposedLevelFactory levelFactory) {
        return new LevelManager(this.getLevels(levelFactory));
    }

    @Bean
    public ComposedLevelFactory getComposedLevelFactory(ConversionService conversionService, MessageSource messageSource) {
        return new ComposedLevelFactory(conversionService, messageSource, new AtomBuilder(conversionService));
    }

    private List<Level> getLevels(ComposedLevelFactory levelFactory) {
        List<Level> levels = Lists.newArrayList();
        levels.add(levelFactory.createRandom("intro", new IntroValidator()));
        levels.add(levelFactory.createRandom("join", new JoinValidator(BasicType.A)));
        levels.add(levelFactory.createRandom("pair", new PairValidator(BasicType.E, BasicType.C)));
        levels.add(levelFactory.create("linecs", new LineCsValidator()));
        levels.add(levelFactory.createRandom("joinall", new JoinAllValidator()));
        levels.add(levelFactory.create("connectcorners", new ConnectCornersValidator()));
        levels.add(levelFactory.createRandom("abcdefchains", new AbcdefChainsValidator()));
        levels.add(levelFactory.createRandom("joinsame", new JoinSameValidator()));
        levels.add(levelFactory.create("matchtemplate", new MatchTemplateValidator()));
        levels.add(levelFactory.create("breakmolecule", new BreakMoleculeValidator()));
        levels.add(levelFactory.create("bondprisoner", new BondPrisonerValidator()));
        levels.add(levelFactory.create("passmessage", new PassMessageValidator()));
        levels.add(levelFactory.create("splitladder", new SplitLadderValidator()));
        levels.add(levelFactory.create("insertatom", new InsertAtomValidator()));
        levels.add(levelFactory.create("makeladder", new MakeLadderValidator()));
        levels.add(levelFactory.create("selfrep", new SelfrepValidator()));
        levels.add(levelFactory.create("growmembrane", new GrowMembraneValidator()));
        levels.add(levelFactory.create("membranetransport", new MembraneTransportValidator()));
        levels.add(levelFactory.create("membranedivision", new MembraneTransportValidator()));
        levels.add(levelFactory.create("celldivision", new CellDivisionValidator()));
        levels.add(levelFactory.createWithConstructor("playground", this.randomConstructor(), new IntroValidator()));
        return levels;
    }

    private RandomConstructor randomConstructor() {
        return new RandomConstructor(new uk.org.squirm3.model.Configuration(4000, 4000));
    }
}
