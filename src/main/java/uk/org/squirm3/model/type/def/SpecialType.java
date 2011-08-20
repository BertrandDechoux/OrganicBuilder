package uk.org.squirm3.model.type.def;

import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.BuilderType;

public enum SpecialType implements AtomType, BuilderType {
    KILLER;

    @Override
    public char getCharacterIdentifier() {
        return name().charAt(0);
    }

}
