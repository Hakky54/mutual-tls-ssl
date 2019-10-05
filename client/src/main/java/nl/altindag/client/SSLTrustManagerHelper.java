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

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SSLTrustManagerHelper {

    private String keyStore;
    private String keyStorePassword;
    private String trustStore;
    private String trustStorePassword;

    public SSLTrustManagerHelper(@Value("${client.ssl.mutual-authentication-enabled:false}") boolean mutualAuthenticationEnabled,
                                 @Value("${client.ssl.key-store:}") String keyStore,
                                 @Value("${client.ssl.key-store-password:}") String keyStorePassword,
                                 @Value("${client.ssl.trust-store:}") String trustStore,
                                 @Value("${client.ssl.trust-store-password:}") String trustStorePassword) {
        if (mutualAuthenticationEnabled && (isBlank(keyStore) || isBlank(keyStorePassword) || isBlank(trustStore) || isBlank(trustStorePassword))) {
            throw new ClientException("TrustStore or KeyStore details are empty, which are required to be present when SSL is enabled");
        }

        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
    }

    @Bean
    @ConditionalOnProperty(name = "client.ssl.mutual-authentication-enabled")
    public SSLContext clientSSLContext() {
        try {
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(trustStore, trustStorePassword);
            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keyStore, keyStorePassword);

            return getSSLContext(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | KeyManagementException e) {
            throw new ClientException(e);
        }
    }

    private static SSLContext getSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private static KeyManagerFactory getKeyManagerFactory(String keystorePath, String keystorePassword) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        KeyStore keyStore = loadKeyStore(keystorePath, keystorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());
        return keyManagerFactory;
    }

    private static TrustManagerFactory getTrustManagerFactory(String truststorePath, String truststorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore trustStore = loadKeyStore(truststorePath, truststorePassword);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
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

}
