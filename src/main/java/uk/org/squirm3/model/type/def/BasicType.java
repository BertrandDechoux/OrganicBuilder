package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.model.type.ReactionType;

public enum BasicType implements AtomType, BuilderType, ReactionType {
    A, B, C, D, E, F;

    @Override
    public char getCharacterIdentifier() {
        return (char) ('a' + ordinal());
    }
    
    @Override
    public int getIntegerIndentifier() {
        return ordinal();
    }
}
