package nl.altindag.client;

import nl.altindag.ssl.SSLFactory;
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

class SSLConfigShould {

    private final SSLConfig victim = new SSLConfig();

    @Test
    void createSslFactoryWithOneWayAuthentication() {
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLFactory sslFactory = victim.sslFactory(true, false,
                EMPTY, EMPTY.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslFactory).isNotNull();
        assertThat(sslFactory.getSslContext()).isNotNull();
        assertThat(sslFactory.getKeyManager()).isNotPresent();
        assertThat(sslFactory.getTrustManager()).isPresent();
    }

    @Test
    void createSslFactoryWithTwoWayAuthentication() {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLFactory sslFactory = victim.sslFactory(false, true,
                keyStorePath, keyStorePassword.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslFactory).isNotNull();
        assertThat(sslFactory.getSslContext()).isNotNull();
        assertThat(sslFactory.getKeyManager()).isPresent();
        assertThat(sslFactory.getTrustManager()).isPresent();
    }

    @Test
    void notCreateSslFactoryWhenOneWayAuthenticationAndTwoWayAuthenticationIsDisabled() {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLFactory sslFactory = victim.sslFactory(false, false,
                keyStorePath, keyStorePassword.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslFactory).isNotNull();
        assertThat(sslFactory.getSslContext()).isNotNull();
        assertThat(sslFactory.getKeyManager()).isNotPresent();
        assertThat(sslFactory.getTrustManager()).isPresent();
    }

}
