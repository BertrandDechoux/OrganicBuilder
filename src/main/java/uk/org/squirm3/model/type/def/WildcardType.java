package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.ReactionType;

public enum WildcardType implements ReactionType {
    X('x', 30), Y('y', 31), Z('z', 32);

    private char character;
    private int integer;

    private WildcardType(final char character, final int integer) {
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
