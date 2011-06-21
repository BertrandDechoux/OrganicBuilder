package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.BuilderType;

public enum SpecialType implements AtomType, BuilderType {
    KILLER('K', -1);

    private char character;
    private int integer;

    private SpecialType(final char character, final int integer) {
        this.character = character;
        this.integer = integer;
    }

    @Override
    public char getCharacterIdentifier() {
        return character;
    }

    @Override
    public int getIntegerIndentifier() {
        return integer;
    }
}
