package uk.org.squirm3.engine.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.IPhysicalPoint;
import uk.org.squirm3.model.MobilePoint;

public abstract class AbstractAtomGenerator implements AtomGenerator {

    private final Configuration configuration;

    public AbstractAtomGenerator(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    final public Collection<Atom> generate() throws GeneratorException {
        return Collections.unmodifiableList(new ArrayList<Atom>(Arrays
                .asList(createAtoms_internal())));
    }

    protected abstract Atom[] createAtoms_internal();

    protected final Configuration getConfiguration() {
        return configuration;
    }

    protected static final void setRandomSpeed(
            final IPhysicalPoint iPhysicalPoint) {
        final float ms = Atom.getAtomSize() / 3;
        iPhysicalPoint.setSpeedX((float) (Math.random() * ms - ms / 2.0));
        iPhysicalPoint.setSpeedY((float) (Math.random() * ms - ms / 2.0));
    }

    protected static final boolean createAtoms(final int numberOfAtoms,
            final int[] types, final float x0, final float x1, final float y0,
            final float y1, final Atom[] atoms) {
        if (types.length < 1 || numberOfAtoms > atoms.length) {
            return false;
        }
        final float atomSize = Atom.getAtomSize();

        // check that enough space will be let to allow clean reactions
        final int evaluation = (int) ((x1 - x0) / (atomSize * 3))
                * (int) ((y1 - y0) / (atomSize * 3));
        if (evaluation < numberOfAtoms) {
            return false;
        }

        // creation of the atoms
        final IPhysicalPoint iPhysicalPoint = new MobilePoint();
        int n = atoms.length - numberOfAtoms;

        for (float x = x0 + 2 * atomSize; x < x1 - 2 * atomSize
                && n < atoms.length; x += 3 * atomSize) {
            for (float y = y0 + 2 * atomSize; y < y1 - 2 * atomSize
                    && n < atoms.length; y += 3 * atomSize) {
                iPhysicalPoint.setPositionX(x);
                iPhysicalPoint.setPositionY(y);
                setRandomSpeed(iPhysicalPoint);
                atoms[n] = new Atom(iPhysicalPoint, types[n % types.length], 0);
                n++;
            }
        }
        return true;
    }

}
