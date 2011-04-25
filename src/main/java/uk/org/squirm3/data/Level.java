package uk.org.squirm3.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

public abstract class Level implements ILevel {

    // implementation of the ILevel interface
    public List<String> getErrors() {
        return Collections.unmodifiableList(new ArrayList<String>(Arrays
                .asList(errors)));
    }

    public List<Atom> generateAtoms() {
        return Collections.unmodifiableList(new ArrayList<Atom>(Arrays
                .asList(createAtoms(null))));
    }

    public List<Atom> generateAtoms(final Configuration configuration) {
        return Collections.unmodifiableList(new ArrayList<Atom>(Arrays
                .asList(createAtoms(configuration))));
    }

    public boolean isEvaluable() {
        return true;
    }

    public String evaluate(final Collection<? extends Atom> atoms) {
        return evaluate(atoms.toArray(new Atom[]{}));
    }

    // end of pseudo implementation

    public static final int[] TYPES = {0, 1, 2, 3, 4, 5};

    private final String title, challenge, hint;
    private final String[] errors;
    private Configuration configuration;
    private final Configuration defaultConfiguration;

    public Level(final MessageSource messageSource, final String key,
            final Configuration defaultConfiguration) {
        final String title = localize(messageSource, key + ".title");
        final String challenge = localize(messageSource, key + ".challenge");
        final String hint = localize(messageSource, key + ".hint");
        final int numberOfErrors = Integer.parseInt(localize(messageSource, key
                + ".errors"));
        final String[] errors = new String[numberOfErrors];
        for (int j = 1; j <= numberOfErrors; j++) {
            errors[j - 1] = localize(messageSource, key + ".error." + new Integer(j));
        }

        this.title = title;
        this.challenge = challenge;
        this.hint = hint;
        this.errors = new String[errors.length];
        System.arraycopy(errors, 0, this.errors, 0, this.errors.length);
        this.defaultConfiguration = defaultConfiguration;
    }

    public static String localize(final MessageSource messageSource,
            final String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }

    final public Atom[] createAtoms(final Configuration configuration) {
        if (configuration == null) {
            return createAtoms_internal(getConfiguration());
        }
        final Atom[] atoms = createAtoms_internal(configuration);
        if (atoms != null) {
            setConfiguration(configuration);
        }
        return atoms;
    }

    protected abstract Atom[] createAtoms_internal(Configuration configuration);

    public abstract String evaluate(Atom[] atoms);

    public String getTitle() {
        return title;
    }

    public String getChallenge() {
        return challenge;
    }

    public String getHint() {
        return hint;
    }

    public Configuration getDefaultConfiguration() {
        return defaultConfiguration;
    }

    public Configuration getConfiguration() {
        return configuration == null ? defaultConfiguration : configuration;
    }

    protected String getError(final int number) {
        return errors[number - 1]; // indices in level*.properties are one-based
    }

    protected void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    protected static void setRandomSpeed(final IPhysicalPoint iPhysicalPoint) {
        final float ms = Atom.getAtomSize() / 3;
        iPhysicalPoint.setSpeedX((float) (Math.random() * ms - ms / 2.0));
        iPhysicalPoint.setSpeedY((float) (Math.random() * ms - ms / 2.0));
    }

    protected static boolean createAtoms(final int numberOfAtoms,
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
