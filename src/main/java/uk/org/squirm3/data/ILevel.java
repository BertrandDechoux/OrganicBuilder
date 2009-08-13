package uk.org.squirm3.data;

import java.util.Collection;
import java.util.List;

/**
 * ${my.copyright}
 */
public interface ILevel {

    public String getTitle();
    public String getDescription();
    public String getHint();
    public List<? extends String> getErrors();
    public Configuration getDefaultConfiguration();

    public Collection<? extends Atom> generateAtoms();
    public Collection<? extends Atom> generateAtoms(Configuration configuration);

    public boolean isEvaluable();
    public String evaluate(Collection<? extends Atom> atoms);
}
