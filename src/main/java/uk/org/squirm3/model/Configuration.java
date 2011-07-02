package uk.org.squirm3.model;

import com.google.common.base.Objects;

public final class Configuration {
    private final float width, height;
    private final int[] types;
    public static final int[] TYPES = {0, 1, 2, 3, 4, 5};

    public Configuration(final float width, final float height) {
        final int[] types_copy = new int[TYPES.length];
        System.arraycopy(TYPES, 0, types_copy, 0, TYPES.length);
        types = types_copy;
        this.width = width;
        this.height = height;
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
        return Objects.toStringHelper(this).add("width", width)
                .add("height", height).toString();
    }
}
