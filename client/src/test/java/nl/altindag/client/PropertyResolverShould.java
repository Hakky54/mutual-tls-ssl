package nl.altindag.client;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyResolverShould {

    @Test
    @SuppressWarnings("AccessStaticViaInstance")
    void loadProperties() {
        LogCaptor logCaptor = LogCaptor.forClass(YamlPropertiesFactoryBean.class);
        PropertySourcesPlaceholderConfigurer properties = new PropertyResolver().properties();

        assertThat(properties).isNotNull();

        List<String> debugLogs = logCaptor.getDebugLogs();

        assertThat(debugLogs).hasSize(3);
        assertThat(debugLogs.get(0)).isEqualTo("Loading from YAML: class path resource [application.yml]");
        assertThat(debugLogs.get(1)).containsSubsequence("Merging document (no matchers set): {spring={main={banner-mode=off, web-application-type=none}}, ");
        assertThat(debugLogs.get(2)).isEqualTo("Loaded 1 document from YAML resource: class path resource [application.yml]");
    }

}
