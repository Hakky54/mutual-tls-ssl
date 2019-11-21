package nl.altindag.client;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

@SuppressWarnings({ "UnnecessaryLocalVariable", "ConstantConditions" })
public class SSLContextHelperShould {

    @Test
    public void notCreateSSLContextIfOneWayAndTwoWayAuthenticationIsDisabled() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = EMPTY;
        String trustStorePassword = EMPTY;

        SSLContextHelper sslContextHelper = new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                 keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslContextHelper.isSecurityEnabled()).isFalse();
        assertThat(sslContextHelper.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslContextHelper.isTwoWayAuthenticationEnabled()).isFalse();
        assertThat(sslContextHelper.getSslContext()).isNull();

        assertThat(sslContextHelper.getKeyManagerFactory()).isNull();
        assertThat(sslContextHelper.getKeyStore()).isNull();

        assertThat(sslContextHelper.getTrustManagerFactory()).isNull();
        assertThat(sslContextHelper.getTrustStore()).isNull();
    }

    @Test
    public void createSSLContextWithClientIdentity() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLContextHelper sslContextHelper = new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                 keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslContextHelper.isSecurityEnabled()).isTrue();
        assertThat(sslContextHelper.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslContextHelper.isTwoWayAuthenticationEnabled()).isTrue();
        assertThat(sslContextHelper.getSslContext()).isNotNull();

        assertThat(sslContextHelper.getKeyManagerFactory()).isNotNull();
        assertThat(sslContextHelper.getKeyManagerFactory().getKeyManagers()).isNotEmpty();
        assertThat(sslContextHelper.getKeyStore()).isNotNull();

        assertThat(sslContextHelper.getTrustManagerFactory()).isNotNull();
        assertThat(sslContextHelper.getTrustManagerFactory().getTrustManagers()).isNotEmpty();
        assertThat(sslContextHelper.getTrustStore()).isNotNull();
    }

    @Test
    public void createSSLContextWithClientTrustStore() {
        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLContextHelper sslContextHelper = new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                 keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslContextHelper.isSecurityEnabled()).isTrue();
        assertThat(sslContextHelper.isOneWayAuthenticationEnabled()).isTrue();
        assertThat(sslContextHelper.isTwoWayAuthenticationEnabled()).isFalse();
        assertThat(sslContextHelper.getSslContext()).isNotNull();

        assertThat(sslContextHelper.getTrustManagerFactory()).isNotNull();
        assertThat(sslContextHelper.getTrustManagerFactory().getTrustManagers()).isNotEmpty();
        assertThat(sslContextHelper.getTrustStore()).isNotNull();
    }

    @Test
    public void createSSLContextWithTlsProtocolVersionOneDotTwo() {
        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLContextHelper sslContextHelper = new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                 keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslContextHelper.getSslContext()).isNotNull();
        assertThat(sslContextHelper.getSslContext().getProtocol()).isEqualTo("TLSv1.3");
    }

    @Test
    public void throwExceptionWhenKeyStoreFileIsNotFound() {
        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/not-existing-truststore.jks";
        String trustStorePassword = "secret";

        assertThatThrownBy(() -> new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath, keyStorePassword, trustStorePath, trustStorePassword))
                .isInstanceOf(ClientException.class)
                .hasMessage("Could not find the keystore file with the given location keystores-for-unit-tests/not-existing-truststore.jks");
    }

    @Test
    public void throwExceptionOneWayAuthenticationIsEnabledWhileTrustStorePathIsNotProvided() {
        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = EMPTY;
        String trustStorePassword = "secret";

        assertThatThrownBy(() -> new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath, keyStorePassword, trustStorePath, trustStorePassword))
                .isInstanceOf(ClientException.class)
                .hasMessage("TrustStore details are empty, which are required to be present when SSL is enabled");
    }

    @Test
    public void throwExceptionOneWayAuthenticationIsEnabledWhileTrustStorePasswordIsNotProvided() {
        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = EMPTY;

        assertThatThrownBy(() -> new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath, keyStorePassword, trustStorePath, trustStorePassword))
                .isInstanceOf(ClientException.class)
                .hasMessage("TrustStore details are empty, which are required to be present when SSL is enabled");
    }

    @Test
    public void throwExceptionTwoWayAuthenticationEnabledWhileKeyStorePathIsNotProvided() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = EMPTY;
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        assertThatThrownBy(() -> new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath, keyStorePassword, trustStorePath, trustStorePassword))
                .isInstanceOf(ClientException.class)
                .hasMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");
    }

    @Test
    public void throwExceptionTwoWayAuthenticationEnabledWhileKeyStorePasswordIsNotProvided() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        assertThatThrownBy(() -> new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath, keyStorePassword, trustStorePath, trustStorePassword))
                .isInstanceOf(ClientException.class)
                .hasMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");
    }

    @Test
    public void throwExceptionTwoWayAuthenticationEnabledWhileTrustStorePathIsNotProvided() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = EMPTY;
        String trustStorePassword = "secret";

        assertThatThrownBy(() -> new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath, keyStorePassword, trustStorePath, trustStorePassword))
                .isInstanceOf(ClientException.class)
                .hasMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");
    }

    @Test
    public void throwExceptionTwoWayAuthenticationEnabledWhileTrustStorePasswordIsNotProvided() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = EMPTY;

        assertThatThrownBy(() -> new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath, keyStorePassword, trustStorePath, trustStorePassword))
                .isInstanceOf(ClientException.class)
                .hasMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");
    }

    @Test
    public void throwExceptionWhenX509TrustManagerIsRequestWhenSecurityIsDisabled() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = EMPTY;
        String trustStorePassword = EMPTY;

        SSLContextHelper sslContextHelper = new SSLContextHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                 keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThatThrownBy(sslContextHelper::getX509TrustManager)
                .isInstanceOf(ClientException.class)
                .hasMessage("The TrustManager could not be provided because it is not available");
    }

}
