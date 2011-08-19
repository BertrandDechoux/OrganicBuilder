package uk.org.squirm3.ui;

import java.awt.GridBagConstraints;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;


public abstract class SwingUtils {

    public static GridBagConstraints createCustomGBC(final int x, final int y, final double weightx, final int fill) {
        final GridBagConstraints gbc = SwingUtils.createCustomGBC(x,y);
        gbc.weightx = weightx;
        gbc.fill = fill;
        return gbc;
    }

    public static GridBagConstraints createCustomGBC(final int x, final int y) {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

    public static JFormattedTextField createIntegerTextField(final int min, final int max,
            final int now, final int columnNumber) {
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
