package nl.altindag.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
public class SSLTrustManagerHelper {

    @Value("${client.ssl.key-store}")
    private String keyStore;

    @Value("${client.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${client.ssl.trust-store}")
    private String trustStore;

    @Value("${client.ssl.trust-store-password}")
    private String trustStorePassword;

    @Bean
    @ConditionalOnProperty(name = "client.ssl.enabled")
    public SSLContext clientSSLContext() {
        try {
            TrustManagerFactory trustManagerFactory = getTrustManagerFactory(trustStore, trustStorePassword);

            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keyStore, keyStorePassword);

            return getSSLContext(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers());
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static SSLContext getSSLContext(KeyManager[] keyManagers, TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private static KeyManagerFactory getKeyManagerFactory(String keystorePath, String keystorePassword) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

        keystore.load(SSLTrustManagerHelper.class.getClassLoader().getResourceAsStream(keystorePath), keystorePassword.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword.toCharArray());
        return keyManagerFactory;
    }

    private static TrustManagerFactory getTrustManagerFactory(String truststorePath, String truststorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        trustStore.load(SSLTrustManagerHelper.class.getClassLoader().getResourceAsStream(truststorePath), truststorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); // PKIX
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

}
