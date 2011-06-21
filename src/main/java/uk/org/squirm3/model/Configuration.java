package uk.org.squirm3.model;

import com.google.common.base.Objects;

public final class Configuration {
    private final int numberOfAtoms;
    private final float width, height;
    private final int[] types;
    // end of pseudo implementation

    public static final int[] TYPES = {0, 1, 2, 3, 4, 5};

    public Configuration(final int numberOfAtoms, final float width,
            final float height) {
        this.numberOfAtoms = numberOfAtoms;
        final int[] types_copy = new int[TYPES.length];
        System.arraycopy(TYPES, 0, types_copy, 0, TYPES.length);
        types = types_copy;
        this.width = width;
        this.height = height;
    }

    public int getNumberOfAtoms() {
        return numberOfAtoms;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int[] getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("numberOfAtoms", numberOfAtoms)
                .add("width", width).add("height", height).toString();
    }
}
