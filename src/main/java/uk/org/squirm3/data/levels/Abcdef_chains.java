package uk.org.squirm3.data.levels;

import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.Configuration;
import uk.org.squirm3.data.Level;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * ${my.copyright}
 */
public class Abcdef_chains extends Level {

    public Abcdef_chains(String title, String challenge, String hint, String[] errors,
                         Configuration defaultConfiguration) {
        super(title, challenge, hint, errors, defaultConfiguration);
    }

    protected Atom[] createAtoms_internal(Configuration configuration) {
        Atom[] atoms = new Atom[configuration.getNumberOfAtoms()];
        if (createAtoms(configuration.getNumberOfAtoms(), configuration.getTypes(), 0, configuration.getWidth(), 0, configuration.getHeight(), atoms))
            return atoms;
        else return null;
    }

    public String evaluate(Atom[] atoms) {
        // how many abcdef chains are there?
        int num_abcdef_chains_found = 0;
        for (int i = 0; i < atoms.length; i++) {
            Atom a = atoms[i];
            if (a.getType() == 0 && a.getBonds().size() == 1) {
                // looks promising - let's check
                LinkedList joined = new LinkedList();
                a.getAllConnectedAtoms(joined);
                if (joined.size() != 6) continue;
                Iterator it = joined.iterator();
                if (((Atom) it.next()).getType() == 0 && ((Atom) it.next()).getType() == 1
                        && ((Atom) it.next()).getType() == 2 && ((Atom) it.next()).getType() == 3
                        && ((Atom) it.next()).getType() == 4 && ((Atom) it.next()).getType() == 5)
                    num_abcdef_chains_found++;
                // (this isn't a perfect test but hopefully close enough)
            }
        }
        if (num_abcdef_chains_found == 0) return getError(1);
        else if (num_abcdef_chains_found == 1) return getError(2);
        return null;
    }
}
