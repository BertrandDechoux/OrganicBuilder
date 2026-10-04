package uk.org.squirm3.ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * Dark theme modeled on the Evoloom web app: near-black, slightly warm
 * surfaces with a gold membrane accent. FlatLaf loads
 * {@code EvoloomLaf.properties} from this package on top of the
 * {@link FlatDarkLaf} base palette.
 */
public class EvoloomLaf extends FlatDarkLaf {

    public static boolean setup() {
        return setup(new EvoloomLaf());
    }

    @Override
    public String getName() {
        return "Evoloom";
    }
}
