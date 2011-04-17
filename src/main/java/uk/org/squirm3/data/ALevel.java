package uk.org.squirm3.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ALevel implements ILevel {

    private final String title, challenge, hint;
    private final List<String> errors;
    private Configuration configuration;
    private final Configuration defaultConfiguration;

    public ALevel(final String title, final String challenge,
            final String hint, final List<String> errors,
            final Configuration defaultConfiguration) {
        this.title = title;
        this.challenge = challenge;
        this.hint = hint;
        this.errors = Collections
                .unmodifiableList(new ArrayList<String>(errors));
        this.defaultConfiguration = defaultConfiguration;
    }

    public final String getTitle() {
        return title;
    }

    public final String getChallenge() {
        return challenge;
    }

    public final String getHint() {
        return hint;
    }

    public final List<String> getErrors() {
        return errors;
    }

    public final Configuration getConfiguration() {
        return configuration == null ? defaultConfiguration : configuration;
    }

    public final Configuration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public final List<Atom> generateAtoms() {
        return generateAtoms(defaultConfiguration);
    }

    public final List<Atom> generateAtoms(final Configuration configuration) {
        List<Atom> atoms = _generateAtoms(configuration);
        if (atoms != null) {
            final double r = Atom.getAtomSize();
            final int max = (int) (configuration.getHeight()
                    * configuration.getWidth() / (4 * r * r));
            if (atoms.size() > max) {
                atoms = null;
            }
        }
        if (atoms != null) {
            this.configuration = configuration;
            return atoms;
        } else {
            throw new RuntimeException(new IllegalArgumentException());
        }
    }

    protected abstract List<Atom> _generateAtoms(Configuration configuration);

    public boolean isEvaluable() {
        return false;
    }

    public String evaluate(final Collection<? extends Atom> atoms) {
        throw new RuntimeException();
    }

    protected static void setRandomSpeed(final IPhysicalPoint iPhysicalPoint) {
        final float ms = Atom.getAtomSize() / 3;
        iPhysicalPoint.setSpeedX((float) (Math.random() * ms - ms / 2.0));
        iPhysicalPoint.setSpeedY((float) (Math.random() * ms - ms / 2.0));
    }

    protected static boolean fill(final int numberOfAtoms,
            final List<Integer> types, final float x0, final float x1,
            final float y0, final float y1, final List<Atom> atoms) {
        if (types.size() < 1) {
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
        int n = 0;

        for (float x = x0 + 2 * atomSize; x < x1 - 2 * atomSize
                && n < numberOfAtoms; x += 3 * atomSize) {
            for (float y = y0 + 2 * atomSize; y < y1 - 2 * atomSize
                    && n < numberOfAtoms; y += 3 * atomSize) {
                iPhysicalPoint.setPositionX(x);
                iPhysicalPoint.setPositionY(y);
                setRandomSpeed(iPhysicalPoint);
                atoms.add(new Atom(iPhysicalPoint, types.get(n % types.size()),
                        0));
                n++;
            }
        }
        return n == numberOfAtoms;
    }
}
