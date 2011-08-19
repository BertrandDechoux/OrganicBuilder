package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.ReactionType;

public enum WildcardType implements ReactionType {
    X('x', 6), Y('y', 7); // , Z('z', 8);

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
