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

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("prototype")
public class SSLTrustManagerHelper {

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
    private DefaultHostnameVerifier defaultHostnameVerifier = new DefaultHostnameVerifier();

    public SSLTrustManagerHelper(@Value("${client.ssl.one-way-authentication-enabled:false}") boolean oneWayAuthenticationEnabled,
                                 @Value("${client.ssl.two-way-authentication-enabled:false}") boolean twoWayAuthenticationEnabled,
                                 @Value("${client.ssl.key-store:}") String keyStorePath,
                                 @Value("${client.ssl.key-store-password:}") String keyStorePassword,
                                 @Value("${client.ssl.trust-store:}") String trustStorePath,
                                 @Value("${client.ssl.trust-store-password:}") String trustStorePassword) {
        if (oneWayAuthenticationEnabled && (isBlank(trustStorePath) || isBlank(trustStorePassword))) {
            throw new ClientException("TrustStore details are empty, which are required to be present when SSL is enabled");
        }

        if (twoWayAuthenticationEnabled && (isBlank(keyStorePath) || isBlank(keyStorePassword) || isBlank(trustStorePath) || isBlank(trustStorePassword))) {
            throw new ClientException("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");
        }

        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
        this.twoWayAuthenticationEnabled = twoWayAuthenticationEnabled;
        this.oneWayAuthenticationEnabled = oneWayAuthenticationEnabled;

        if (oneWayAuthenticationEnabled || twoWayAuthenticationEnabled) {
            securityEnabled = true;
        }

        if (oneWayAuthenticationEnabled) {
            sslContext = createSSLContextWithClientTrustStore();
        }

        if (twoWayAuthenticationEnabled) {
            sslContext = createSSLContextWithClientKeyStoreAndTrustStore();
        }
    }

    private SSLContext createSSLContextWithClientTrustStore() {
        try {
            return getSSLContext(null, getTrustManagerFactory(trustStorePath, trustStorePassword).getTrustManagers());
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException | CertificateException e) {
            throw new ClientException(e);
        }
    }

    private SSLContext createSSLContextWithClientKeyStoreAndTrustStore() {
        try {
            return getSSLContext(getKeyManagerFactory(keyStorePath, keyStorePassword).getKeyManagers(),
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
        keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());
        return keyManagerFactory;
    }

    protected TrustManagerFactory getTrustManagerFactory(String truststorePath, String truststorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        trustStore = loadKeyStore(truststorePath, truststorePassword);
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    private static KeyStore loadKeyStore(String keystorePath, String keystorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try(InputStream keystoreInputStream = SSLTrustManagerHelper.class.getClassLoader().getResourceAsStream(keystorePath)) {
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

    public DefaultHostnameVerifier getDefaultHostnameVerifier() {
        return defaultHostnameVerifier;
    }

}
