package nl.altindag.server.util;

import nl.altindag.server.model.ApplicationProperty;
import nl.altindag.ssl.SSLFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SSLFactoryUtilsShould {

    @Test
    void createSSLFactory() throws IOException {
        ApplicationProperty applicationProperty = ApplicationPropertyUtils.readApplicationProperties("application-two-way-authentication.properties");
        SSLFactory sslFactory = SSLFactoryUtils.createSSLFactory(applicationProperty);

        assertThat(sslFactory.getSslContext()).isNotNull();

        assertThat(sslFactory.getKeyManager()).isPresent();
        assertThat(sslFactory.getTrustManager()).isPresent();
    }

}
