package uk.org.squirm3.springframework.converter;

import javax.swing.Icon;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import uk.org.squirm3.swing.GraphicsFactory;

@Component
public class StringToIconConverter implements Converter<String, Icon> {
    private final GraphicsFactory graphicsFactory = new GraphicsFactory();

    @Override
    public Icon convert(final String source) {
        try {
            return graphicsFactory.createIcon(source);
        } catch (final InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
