package nl.altindag.server.util;

import nl.altindag.server.model.ApplicationProperty;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationPropertyUtilsShould {

    @Test
    void readApplicationProperties() throws IOException {
        ApplicationProperty applicationProperty = ApplicationPropertyUtils.readApplicationProperties("application-two-way-authentication.properties");

        assertThat(applicationProperty.isSslEnabled()).isTrue();
        assertThat(applicationProperty.getServerPort()).isEqualTo("8443");
        assertThat(applicationProperty.getKeystorePath()).isEqualTo("identity.jks");
        assertThat(applicationProperty.getKeystorePassword()).isEqualTo("secret".toCharArray());
        assertThat(applicationProperty.getTruststorePath()).isEqualTo("truststore.jks");
        assertThat(applicationProperty.getTruststorePassword()).isEqualTo("secret".toCharArray());

    }

}
