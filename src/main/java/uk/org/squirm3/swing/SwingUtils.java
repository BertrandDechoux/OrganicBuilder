package uk.org.squirm3.swing;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * Shortcuts for swing.
 */
public abstract class SwingUtils {

    /**
     * Custom creation method for {@link JFormattedTextField}.
     */
    public static JFormattedTextField createIntegerTextField(final int min,
            final int max, final int now, final int columnNumber) {
        final NumberFormatter formatter = new NumberFormatter(
                NumberFormat.getIntegerInstance());
        formatter.setMinimum(min);
        formatter.setMaximum(max);
        final JFormattedTextField TF = new JFormattedTextField(formatter);
        TF.setValue(now);
        TF.setColumns(columnNumber);
        return TF;
    }

}
