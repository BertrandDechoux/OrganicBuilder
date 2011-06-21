package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.BuilderType;

public enum RandomBuilderType implements BuilderType {
    RANDOM;

    @Override
    public char getCharacterIdentifier() {
        return 'R';
    }

    @Override
    public int getIntegerIndentifier() {
        return 20 + ordinal();
    }
}
