package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.ReactionType;

public enum WildcardType implements ReactionType {
    X, Y; // , Z;

    @Override
    public char getCharacterIdentifier() {
        return name().toLowerCase().charAt(0);
    }

}
