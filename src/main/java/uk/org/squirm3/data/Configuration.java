package uk.org.squirm3.data;

public final class Configuration {
    private final int numberOfAtoms;
    private final float width, height;
    private final int[] types;

    public Configuration(final int numberOfAtoms, final int[] types,
            final float width, final float height) {
        this.numberOfAtoms = numberOfAtoms;
        final int[] types_copy = new int[types.length];
        System.arraycopy(types, 0, types_copy, 0, types.length);
        this.types = types_copy;
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
}
