package uk.org.squirm3.springframework.converter;

import java.io.IOException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@Component
public class ResourceToStringConverter implements Converter<Resource, String> {

    @Override
    public String convert(final Resource source) {
        try {
            return Resources.toString(source.getURL(), Charsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to convert resource "
                    + source.getFilename(), e);
        }
    }

}
