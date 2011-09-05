package uk.org.squirm3.springframework.converter;

import java.awt.Image;

import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.swing.GraphicsFactory;

public class StringToImageConverter implements Converter<String, Image> {
    private final GraphicsFactory graphicsFactory = new GraphicsFactory();

    @Override
    public Image convert(final String source) {
        try {
            return graphicsFactory.createImage(source);
        } catch (final InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
