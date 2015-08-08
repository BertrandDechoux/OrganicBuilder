package uk.org.squirm3.config;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@ComponentScan("uk.org.squirm3.springframework.converter")
@PropertySource("classpath:configuration.properties")
public class SpringConfig {

    @Bean(name = "conversionService")
    public ConversionService getConversionService(List<Converter<?, ?>> converters) {
        DefaultConversionService conversionService = new DefaultConversionService();
        for (Converter<?, ?> converter : converters) {
            conversionService.addConverter(converter);
        }
        return conversionService;
    }

    @Bean
    public MessageSource getMessageSource(DelegatingMessageSource delegatingMessageSource) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/interface", "i18n/levels");
        delegatingMessageSource.setParentMessageSource(messageSource);
        return messageSource;
    }
}
