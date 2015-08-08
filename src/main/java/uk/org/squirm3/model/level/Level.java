package uk.org.squirm3.model.level;

import java.util.Collection;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;

public interface Level {

    public String getTitle();
    public String getChallenge();
    public String getHint();

    public Configuration construct();

    public String evaluate(Collection<? extends Atom> atoms);
}
