package uk.org.squirm3.data.levels;

import java.util.Random;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.FixedPoint;
import uk.org.squirm3.data.Level;

public class MatchTemplate extends Level {
    
    public MatchTemplate(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "matchtemplate", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        final Random PRNG = new Random(); // a prng for use when resetting atoms
        // place and bond six random atoms to form a template
        for (int i = 0; i < 6; i++) {
            atoms[i] = new Atom(new FixedPoint(size * 1.5f, size * 5.0f + i
                    * size * 2.1f), PRNG.nextInt(6), 1);
            if (i > 0) {
                atoms[i].bondWith(atoms[i - 1]);
            }
        }
        if (createAtoms(configuration.getNumberOfAtoms() - 6,
                configuration.getTypes(), 2.5f * size,
                configuration.getWidth(), 0, configuration.getHeight(), atoms)) {
            return atoms;
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {
        // does each atom 0-5 have another single type-matching atom attached?
        for (int i = 0; i < 6; i++) {
            final Atom a = atoms[i];
            final Atom b = (Atom) a.getBonds().getLast();
            if (b.getType() != a.getType() || b.getBonds().size() != 1) {
                return getError(1);
                // (not a complete test, but hopefully ok)
            }
        }
        return null;
    }
}
