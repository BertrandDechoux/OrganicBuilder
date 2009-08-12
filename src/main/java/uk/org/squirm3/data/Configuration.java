package uk.org.squirm3.data;

/**  
${my.copyright}
 */

public final class Configuration {
    private final int numberOfAtoms;
    private final float width, height;
    private final int[] types;

    public Configuration(int numberOfAtoms, int[] types,
            float width, float height) {
        this.numberOfAtoms = numberOfAtoms;
        int[] types_copy = new int[types.length];
        System.arraycopy(types, 0, types_copy, 0, types.length);
        this.types = types_copy;
        this.width = width;
        this.height = height;
    }

    public int getNumberOfAtoms() { return numberOfAtoms; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int[] getTypes() { return types; }
}
