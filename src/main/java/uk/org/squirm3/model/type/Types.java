package uk.org.squirm3.model.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import uk.org.squirm3.model.type.def.BasicType;
import uk.org.squirm3.model.type.def.RandomBasicType;
import uk.org.squirm3.model.type.def.RandomBuilderType;
import uk.org.squirm3.model.type.def.SpecialType;
import uk.org.squirm3.model.type.def.WildcardType;

public abstract class Types {

    final static Collection<? extends AtomType> atomTypes;
    final static Collection<? extends BuilderType> builderTypes;
    final static Collection<? extends ChemicalType> chemicalTypes;
    final static Collection<? extends ReactionType> reactionTypes;

    static {
        Collection<AtomType> modifiableAtomTypes = new ArrayList<AtomType>();
        modifiableAtomTypes.addAll(Arrays.asList(BasicType.values()));
        modifiableAtomTypes.addAll(Arrays.asList(SpecialType.values()));
        atomTypes = Collections.unmodifiableCollection(modifiableAtomTypes);

        Collection<BuilderType> modifiableBuilderTypes = new ArrayList<BuilderType>();
        modifiableBuilderTypes.addAll(Arrays.asList(BasicType.values()));
        modifiableBuilderTypes.addAll(Arrays.asList(RandomBasicType.values()));
        modifiableBuilderTypes.addAll(Arrays.asList(RandomBuilderType.values()));
        modifiableBuilderTypes.addAll(Arrays.asList(SpecialType.values()));
        builderTypes = Collections.unmodifiableCollection(modifiableBuilderTypes);
        
        Collection<ChemicalType> modifiableChemicalTypes = new ArrayList<ChemicalType>();
        modifiableChemicalTypes.addAll(Arrays.asList(BasicType.values()));
        modifiableChemicalTypes.addAll(Arrays.asList(RandomBasicType.values()));
        modifiableChemicalTypes.addAll(Arrays.asList(RandomBuilderType.values()));
        modifiableChemicalTypes.addAll(Arrays.asList(SpecialType.values()));
        modifiableChemicalTypes.addAll(Arrays.asList(WildcardType.values()));
        chemicalTypes = Collections.unmodifiableCollection(modifiableChemicalTypes);
        
        Collection<ReactionType> modifiableReactionTypes = new ArrayList<ReactionType>();
        modifiableReactionTypes.addAll(Arrays.asList(BasicType.values()));
        modifiableReactionTypes.addAll(Arrays.asList(WildcardType.values()));
        reactionTypes = Collections.unmodifiableCollection(modifiableReactionTypes);
    }

    public static final Collection<? extends AtomType> getAtomTypes() {
        return atomTypes;
    }

    public static final Collection<? extends BuilderType> getBuilderTypes() {
        return builderTypes;
    }

    public static final Collection<? extends ChemicalType> getChemicalTypes() {
        return chemicalTypes;
    }

    public static final Collection<? extends ReactionType> getReactionTypes() {
        return reactionTypes;
    }

}
