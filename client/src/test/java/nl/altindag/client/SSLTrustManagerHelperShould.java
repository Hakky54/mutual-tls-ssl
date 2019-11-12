package nl.altindag.client;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SSLTrustManagerHelperShould {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions"})
    public void notCreateSSLContextIfOneWayAndTwoWayAuthenticationIsDisabled() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = EMPTY;
        String trustStorePassword = EMPTY;

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslTrustManagerHelper.isSecurityEnabled()).isFalse();
        assertThat(sslTrustManagerHelper.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslTrustManagerHelper.isTwoWayAuthenticationEnabled()).isFalse();
        assertThat(sslTrustManagerHelper.getSslContext()).isNull();

        assertThat(sslTrustManagerHelper.getKeyManagerFactory()).isNull();
        assertThat(sslTrustManagerHelper.getKeyStore()).isNull();

        assertThat(sslTrustManagerHelper.getTrustManagerFactory()).isNull();
        assertThat(sslTrustManagerHelper.getTrustStore()).isNull();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void createSSLContextWithClientIdentity() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslTrustManagerHelper.isSecurityEnabled()).isTrue();
        assertThat(sslTrustManagerHelper.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslTrustManagerHelper.isTwoWayAuthenticationEnabled()).isTrue();
        assertThat(sslTrustManagerHelper.getSslContext()).isNotNull();

        assertThat(sslTrustManagerHelper.getKeyManagerFactory()).isNotNull();
        assertThat(sslTrustManagerHelper.getKeyManagerFactory().getKeyManagers()).isNotEmpty();
        assertThat(sslTrustManagerHelper.getKeyStore()).isNotNull();

        assertThat(sslTrustManagerHelper.getTrustManagerFactory()).isNotNull();
        assertThat(sslTrustManagerHelper.getTrustManagerFactory().getTrustManagers()).isNotEmpty();
        assertThat(sslTrustManagerHelper.getTrustStore()).isNotNull();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void createNewInstanceOfSSLTrustManagerHelper() {
        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        SSLTrustManagerHelper sslTrustManagerHelperSecondInstance = sslTrustManagerHelper.createNewInstance();

        assertThat(sslTrustManagerHelper.hashCode()).isNotEqualTo(sslTrustManagerHelperSecondInstance.hashCode());
        assertThat(sslTrustManagerHelper.getSslContext()).isNotEqualTo(sslTrustManagerHelperSecondInstance.getSslContext());
        assertThat(sslTrustManagerHelper.getX509TrustManager()).isNotEqualTo(sslTrustManagerHelperSecondInstance.getX509TrustManager());
        assertThat(sslTrustManagerHelper.getTrustManagerFactory()).isNotEqualTo(sslTrustManagerHelperSecondInstance.getTrustManagerFactory());
        assertThat(sslTrustManagerHelper.getKeyManagerFactory()).isNotEqualTo(sslTrustManagerHelperSecondInstance.getKeyManagerFactory());
        assertThat(sslTrustManagerHelper.getKeyStore()).isNotEqualTo(sslTrustManagerHelperSecondInstance.getKeyStore());
        assertThat(sslTrustManagerHelper.getTrustStore()).isNotEqualTo(sslTrustManagerHelperSecondInstance.getTrustStore());

        assertThat(sslTrustManagerHelper.isSecurityEnabled()).isTrue();
        assertThat(sslTrustManagerHelper.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslTrustManagerHelper.isTwoWayAuthenticationEnabled()).isTrue();

        assertThat(sslTrustManagerHelperSecondInstance.isSecurityEnabled()).isTrue();
        assertThat(sslTrustManagerHelperSecondInstance.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslTrustManagerHelperSecondInstance.isTwoWayAuthenticationEnabled()).isTrue();
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions"})
    public void createSSLContextWithClientTrustStore() {
        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslTrustManagerHelper.isSecurityEnabled()).isTrue();
        assertThat(sslTrustManagerHelper.isOneWayAuthenticationEnabled()).isTrue();
        assertThat(sslTrustManagerHelper.isTwoWayAuthenticationEnabled()).isFalse();
        assertThat(sslTrustManagerHelper.getSslContext()).isNotNull();

        assertThat(sslTrustManagerHelper.getTrustManagerFactory()).isNotNull();
        assertThat(sslTrustManagerHelper.getTrustManagerFactory().getTrustManagers()).isNotEmpty();
        assertThat(sslTrustManagerHelper.getTrustStore()).isNotNull();
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions"})
    public void createSSLContextWithTlsProtocolVersionOneDotTwo() {
        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);

        assertThat(sslTrustManagerHelper.getSslContext()).isNotNull();
        assertThat(sslTrustManagerHelper.getSslContext().getProtocol()).isEqualTo("TLSv1.3");
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions", "unused"})
    public void throwExceptionWhenKeyStoreFileIsNotFound() {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Could not find the keystore file with the given location keystores-for-unit-tests/not-existing-truststore.jks");

        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/not-existing-truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions", "unused"})
    public void throwExceptionOneWayAuthenticationIsEnabledWhileTrustStorePathIsNotProvided() {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("TrustStore details are empty, which are required to be present when SSL is enabled");

        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = EMPTY;
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions", "unused"})
    public void throwExceptionOneWayAuthenticationIsEnabledWhileTrustStorePasswordIsNotProvided() {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("TrustStore details are empty, which are required to be present when SSL is enabled");

        boolean oneWayAuthenticationEnabled = true;
        boolean twoWayAuthenticationEnabled = false;
        String keyStorePath = EMPTY;
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = EMPTY;

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions", "unused"})
    public void throwExceptionTwoWayAuthenticationEnabledWhileKeyStorePathIsNotProvided() {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");

        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = EMPTY;
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions", "unused"})
    public void throwExceptionTwoWayAuthenticationEnabledWhileKeyStorePasswordIsNotProvided() {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");

        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = EMPTY;
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions", "unused"})
    public void throwExceptionTwoWayAuthenticationEnabledWhileTrustStorePathIsNotProvided() {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");

        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = EMPTY;
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
    }

    @Test
    @SuppressWarnings({"UnnecessaryLocalVariable", "ConstantConditions", "unused"})
    public void throwExceptionTwoWayAuthenticationEnabledWhileTrustStorePasswordIsNotProvided() {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");

        boolean oneWayAuthenticationEnabled = false;
        boolean twoWayAuthenticationEnabled = true;
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = EMPTY;

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled,
                                                                                keyStorePath, keyStorePassword, trustStorePath, trustStorePassword);
    }

}
