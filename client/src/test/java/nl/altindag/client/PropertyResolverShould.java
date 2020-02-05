package nl.altindag.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import ch.qos.logback.classic.Level;
import nl.altindag.log.LogCaptor;

public class PropertyResolverShould {

    private PropertyResolver propertyResolver = new PropertyResolver();

    @Test
    public void loadProperties() {
        LogCaptor logCaptor = LogCaptor.forClass(YamlPropertiesFactoryBean.class);
        PropertySourcesPlaceholderConfigurer properties = propertyResolver.properties();

        assertThat(properties).isNotNull();

        assertThat(logCaptor.getLogs(Level.DEBUG)).contains("Loading from YAML: class path resource [application.yml]");
        assertThat(logCaptor.getLogs(Level.DEBUG)).anyMatch(logEntry -> logEntry.contains("Merging document (no matchers set): {spring={main={banner-mode=off, web-application-type=none}}, "));
        assertThat(logCaptor.getLogs(Level.DEBUG)).contains("Loaded 1 document from YAML resource: class path resource [application.yml]");
    }

}
