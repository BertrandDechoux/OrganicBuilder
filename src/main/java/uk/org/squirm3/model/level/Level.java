package uk.org.squirm3.model.level;

import java.util.Collection;
import java.util.List;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;

public interface Level {

    public String getTitle();
    public String getChallenge();
    public String getHint();

    public Configuration getConfiguration();
    public Configuration getDefaultConfiguration();

    public List<Atom> generateAtoms();
    public List<Atom> generateAtoms(Configuration configuration);

    public String evaluate(Collection<? extends Atom> atoms);
}
