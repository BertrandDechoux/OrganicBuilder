package uk.org.squirm3.data;

import java.util.Collection;
import java.util.List;

public interface ILevel {

    public String getTitle();
    public String getChallenge();
    public String getHint();
    public List<String> getErrors();
    public Configuration getConfiguration();
    public Configuration getDefaultConfiguration();

    public List<Atom> generateAtoms();
    public List<Atom> generateAtoms(Configuration configuration);

    public boolean isEvaluable();
    public String evaluate(Collection<? extends Atom> atoms);
}
