package uk.org.squirm3.springframework.converter;

import javax.swing.KeyStroke;

import org.springframework.core.convert.converter.Converter;

public class StringToKeyStrokeConverter implements Converter<String, KeyStroke> {

    @Override
    public KeyStroke convert(final String source) {
        return KeyStroke.getKeyStroke(source);
    }

}
