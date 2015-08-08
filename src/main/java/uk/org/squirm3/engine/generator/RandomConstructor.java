package uk.org.squirm3.engine.generator;

import java.util.ArrayList;
import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Atoms;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.def.BasicType;

public class RandomConstructor implements LevelConstructor {

    final Configuration partialConfiguration;
    final AtomType[] types = BasicType.values();

    private RandomConstructor(final Configuration partialConfiguration) {
        super();
        this.partialConfiguration = partialConfiguration;
    }

    @Override
    public Configuration construct() throws GeneratorException {
        final Collection<Atom> atoms = new ArrayList<Atom>();
        float currentWidth = 5 * Atom.getAtomSize();
        float currentHeight = 5 * Atom.getAtomSize();
        final float maximumWidth = partialConfiguration.getWidth()
                - currentWidth;
        final float maximumHeigth = partialConfiguration.getHeight()
                - currentHeight;
        while (currentHeight < maximumHeigth) {
            if (currentWidth >= maximumWidth) {
                currentWidth = Atom.getAtomSize();
                currentHeight += 6 * Atom.getAtomSize();
            } else {
                atoms.add(Atoms.createMobileAtomWithRandomSpeed(
                        getRandomAtomType(), getRandomState(), currentWidth,
                        currentHeight));
                currentWidth += 6 * Atom.getAtomSize();
            }
        }
        return new Configuration(partialConfiguration.getHeight(),
                partialConfiguration.getWidth(), atoms);
    }

    private AtomType getRandomAtomType() {
        return types[(int) (Math.random() * types.length)];
    }

    private int getRandomState() {
        return (int) (Math.random() + 0.1);
    }

}
