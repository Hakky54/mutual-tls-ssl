package nl.altindag.client;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.util.Objects;

/**
 * Suppressed warning: java:S1118 - "Utility classes should not have public constructors"
 *                                  Spring Framework can't initialize this configuration class without a public constructor
 */
@SuppressWarnings("java:S1118")
@Configuration
public class PropertyResolver {

    private static final String CLIENT_PROPERTY_FILE = "application.yml";

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        var propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        var yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(CLIENT_PROPERTY_FILE));
        propertySourcesPlaceholderConfigurer.setProperties(Objects.requireNonNull(yaml.getObject()));
        return propertySourcesPlaceholderConfigurer;
    }

}
