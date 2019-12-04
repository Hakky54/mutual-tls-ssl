package nl.altindag.client;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;

public class SSLContextHelper {

    private KeyStore keyStore;
    private String keyStorePath;
    private String keyStorePassword;
    private KeyStore trustStore;
    private String trustStorePath;
    private String trustStorePassword;

    private boolean securityEnabled;
    private boolean oneWayAuthenticationEnabled;
    private boolean twoWayAuthenticationEnabled;

    private SSLContext sslContext;
    private TrustManagerFactory trustManagerFactory;
    private KeyManagerFactory keyManagerFactory;
    private HostnameVerifier hostnameVerifier;

    private SSLContextHelper() {}

    private void createSSLContextWithClientTrustStore() {
        try {
            sslContext = getSSLContext(null, getTrustManagerFactory(trustStorePath, trustStorePassword).getTrustManagers());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException | CertificateException e) {
            throw new ClientException(e);
        }
    }

    private void createSSLContextWithClientKeyStoreAndTrustStore() {
        try {
            sslContext = getSSLContext(getKeyManagerFactory(keyStorePath, keyStorePassword).getKeyManagers(),
                                       getTrustManagerFactory(trustStorePath, trustStorePassword).getTrustManagers());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | KeyManagementException e) {
            throw new ClientException(e);
        }
    }

    private static SSLContext getSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private KeyManagerFactory getKeyManagerFactory(String keystorePath, String keystorePassword) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        keyStore = loadKeyStore(keystorePath, keystorePassword);
        keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());
        return keyManagerFactory;
    }

    private TrustManagerFactory getTrustManagerFactory(String truststorePath, String truststorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        trustStore = loadKeyStore(truststorePath, truststorePassword);
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    private static KeyStore loadKeyStore(String keystorePath, String keystorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try(InputStream keystoreInputStream = SSLContextHelper.class.getClassLoader().getResourceAsStream(keystorePath)) {
            if (isNull(keystoreInputStream)) {
                throw new ClientException(String.format("Could not find the keystore file with the given location %s", keystorePath));
            }

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(keystoreInputStream, keystorePassword.toCharArray());
            return keystore;
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    public boolean isOneWayAuthenticationEnabled() {
        return oneWayAuthenticationEnabled;
    }

    public boolean isTwoWayAuthenticationEnabled() {
        return twoWayAuthenticationEnabled;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public TrustManagerFactory getTrustManagerFactory() {
        return trustManagerFactory;
    }

    public KeyManagerFactory getKeyManagerFactory() {
        return keyManagerFactory;
    }

    public X509TrustManager getX509TrustManager() {
        ClientException clientException = new ClientException("The TrustManager could not be provided because it is not available");
        if (isNull(trustManagerFactory)) {
            throw clientException;
        }

        return Arrays.stream(trustManagerFactory.getTrustManagers())
                                                  .filter(trustManager -> trustManager instanceof X509TrustManager)
                                                  .map(trustManager -> (X509TrustManager) trustManager)
                                                  .findFirst()
                                                  .orElseThrow(() -> clientException);
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String keyStorePath;
        private String keyStorePassword;
        private String trustStorePath;
        private String trustStorePassword;

        private boolean oneWayAuthenticationEnabled;
        private boolean twoWayAuthenticationEnabled;
        private boolean hostnameVerifierEnabled = true;

        public Builder withoutSecurity() {
            oneWayAuthenticationEnabled = false;
            twoWayAuthenticationEnabled = false;
            return this;
        }

        public Builder withOneWayAuthentication(String trustStorePath, String trustStorePassword) {
            if (isBlank(trustStorePath) || isBlank(trustStorePassword)) {
                throw new ClientException("TrustStore details are empty, which are required to be present when SSL is enabled");
            }

            this.oneWayAuthenticationEnabled = true;
            this.twoWayAuthenticationEnabled = false;
            this.trustStorePath = trustStorePath;
            this.trustStorePassword = trustStorePassword;
            return this;
        }

        public Builder withTwoWayAuthentication(String keyStorePath, String keyStorePassword, String trustStorePath, String trustStorePassword) {
            if (isBlank(keyStorePath) || isBlank(keyStorePassword) || isBlank(trustStorePath) || isBlank(trustStorePassword)) {
                throw new ClientException("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");
            }

            this.oneWayAuthenticationEnabled = false;
            this.twoWayAuthenticationEnabled = true;
            this.keyStorePath = keyStorePath;
            this.keyStorePassword = keyStorePassword;
            this.trustStorePath = trustStorePath;
            this.trustStorePassword = trustStorePassword;
            return this;
        }

        public Builder withHostnameVerifierEnabled(boolean hostnameVerifierEnabled) {
            this.hostnameVerifierEnabled = hostnameVerifierEnabled;
            return this;
        }

        public SSLContextHelper build() {
            SSLContextHelper sslContextHelper = new SSLContextHelper();
            buildHostnameVerifier(sslContextHelper);
            if (oneWayAuthenticationEnabled || twoWayAuthenticationEnabled) {
                sslContextHelper.securityEnabled = true;
                buildSLLContextForOneWayAuthenticationIfEnabled(sslContextHelper);
                buildSLLContextForTwoWayAuthenticationIfEnabled(sslContextHelper);
            }
            return sslContextHelper;
        }

        private void buildHostnameVerifier(SSLContextHelper sslContextHelper) {
            if (hostnameVerifierEnabled) {
                sslContextHelper.hostnameVerifier = new DefaultHostnameVerifier();
            } else {
                sslContextHelper.hostnameVerifier = new NoopHostnameVerifier();
            }
        }

        private void buildSLLContextForTwoWayAuthenticationIfEnabled(SSLContextHelper sslContextHelper) {
            if (twoWayAuthenticationEnabled) {
                sslContextHelper.twoWayAuthenticationEnabled = true;
                sslContextHelper.keyStorePath = keyStorePath;
                sslContextHelper.keyStorePassword = keyStorePassword;
                sslContextHelper.trustStorePath = trustStorePath;
                sslContextHelper.trustStorePassword = trustStorePassword;
                sslContextHelper.createSSLContextWithClientKeyStoreAndTrustStore();
            }
        }

        private void buildSLLContextForOneWayAuthenticationIfEnabled(SSLContextHelper sslContextHelper) {
            if (oneWayAuthenticationEnabled) {
                sslContextHelper.oneWayAuthenticationEnabled = true;
                sslContextHelper.trustStorePath = trustStorePath;
                sslContextHelper.trustStorePassword = trustStorePassword;
                sslContextHelper.createSSLContextWithClientTrustStore();
            }
        }
    }

}
