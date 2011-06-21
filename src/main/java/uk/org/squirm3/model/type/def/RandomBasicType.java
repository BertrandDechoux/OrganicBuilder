package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.BuilderType;

public enum RandomBasicType implements BuilderType {
    A, B, C, D, E, F;

    @Override
    public char getCharacterIdentifier() {
        return (char) ('A' + ordinal());
    }

    @Override
    public int getIntegerIndentifier() {
        return 10 + ordinal();
    }

}
