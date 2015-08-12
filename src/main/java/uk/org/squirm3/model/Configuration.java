package uk.org.squirm3.model;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.MoreObjects;

public final class Configuration {
    public static final int MAX_NUMBER_OF_STATUS = 50;
    private final double width, height;
    private final Collection<Atom> atoms;

    @SuppressWarnings("unchecked")
    public Configuration(double width, double height) {
        this(width, height, Collections.EMPTY_LIST);
    }

    public Configuration(double width, double height, Collection<Atom> atoms) {
        this.width = width;
        this.height = height;
        this.atoms = atoms;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Collection<Atom> getAtoms() {
        return atoms;
    }

    @Override
    public String toString() {
        return MoreObjects//
                .toStringHelper(this)//
                .add("width", width)//
                .add("height", height)//
                .toString();
    }

}
