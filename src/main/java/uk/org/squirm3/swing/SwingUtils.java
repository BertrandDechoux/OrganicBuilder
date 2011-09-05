package uk.org.squirm3.swing;

import java.awt.GridBagConstraints;
import java.text.NumberFormat;

import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

/**
 * Shortcuts for swing.
 */
public abstract class SwingUtils {

    /**
     * Custom creation method for {@link GridBagConstraints}.
     */
    public static GridBagConstraints createCustomGBC(final int x, final int y,
            final double weightx, final int fill) {
        final GridBagConstraints gbc = createCustomGBC(x, y);
        gbc.weightx = weightx;
        gbc.fill = fill;
        return gbc;
    }

    /**
     * Custom creation method for {@link GridBagConstraints}.
     */
    public static GridBagConstraints createCustomGBC(final int x, final int y) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

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

    /**
     * Custom creation method for {@link JEditorPane}.
     */
    public static JEditorPane createReadOnlyHtmlEditorPane() {
        final JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setContentType("text/html");
        jEditorPane.setEditable(false);
        return jEditorPane;
    }

}
