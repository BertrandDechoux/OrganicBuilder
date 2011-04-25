package uk.org.squirm3.data.levels;

import java.util.LinkedList;

import org.springframework.context.MessageSource;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.FixedPoint;
import uk.org.squirm3.data.Level;

public class ConnectCorners extends Level {

    public ConnectCorners(final MessageSource messageSource,
            final Configuration defaultConfiguration) {
        super(messageSource, "connectcorners", defaultConfiguration);
    }

    @Override
    protected Atom[] createAtoms_internal(final Configuration configuration) {
        final Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        final float size = Atom.getAtomSize();
        atoms[0] = new Atom(new FixedPoint(size * 1.5f, size * 1.5f), 5, 1);
        atoms[1] = new Atom(new FixedPoint(configuration.getWidth() - size
                * 1.5f, configuration.getHeight() - size * 1.5f), 3, 1);
        if (createAtoms(configuration.getNumberOfAtoms() - 2,
                configuration.getTypes(), 0, configuration.getWidth(),
                2 * size, configuration.getHeight() - 2 * size, atoms)) {
            return atoms;
        }
        return null;
    }

    @Override
    public String evaluate(final Atom[] atoms) {
        // 0 joined to 1?
        final LinkedList joined = new LinkedList();
        atoms[0].getAllConnectedAtoms(joined);
        if (!joined.contains(atoms[1])) {
            return getError(1);
        }
        return null;
    }
}
