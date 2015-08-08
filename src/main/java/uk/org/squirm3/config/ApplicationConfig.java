package uk.org.squirm3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import uk.org.squirm3.springframework.SwingUserInterfaceBean;

@Configuration
@Import(EngineConfig.class)
public class ApplicationConfig {

    @Bean
    public SwingUserInterfaceBean getSwingUserInterfaceBean() {
        return new SwingUserInterfaceBean();
    }

}
