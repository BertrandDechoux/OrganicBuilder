package uk.org.squirm3.model;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Objects;

public final class Configuration {
    public static final int MAX_NUMBER_OF_STATUS = 50;
    private final float width, height;
    private final Collection<Atom> atoms;

    @SuppressWarnings("unchecked")
    public Configuration(final float width, final float height) {
        this(width, height, Collections.EMPTY_LIST);
    }

    public Configuration(final float width, final float height,
            final Collection<Atom> atoms) {
        this.width = width;
        this.height = height;
        this.atoms = atoms;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Collection<Atom> getAtoms() {
        return atoms;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("width", width)
                .add("height", height).toString();
    }

}
