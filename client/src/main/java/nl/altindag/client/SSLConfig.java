package nl.altindag.client;

import nl.altindag.sslcontext.SSLFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SSLConfig {

    @Bean
    @Scope("prototype")
    public SSLFactory sslFactory(
            @Value("${client.ssl.one-way-authentication-enabled:false}") boolean oneWayAuthenticationEnabled,
            @Value("${client.ssl.two-way-authentication-enabled:false}") boolean twoWayAuthenticationEnabled,
            @Value("${client.ssl.key-store:}") String keyStorePath,
            @Value("${client.ssl.key-store-password:}") char[] keyStorePassword,
            @Value("${client.ssl.trust-store:}") String trustStorePath,
            @Value("${client.ssl.trust-store-password:}") char[] trustStorePassword) {
        if (!oneWayAuthenticationEnabled && !twoWayAuthenticationEnabled) {
            return null;
        }

        SSLFactory sslFactory = null;

        if (oneWayAuthenticationEnabled) {
            sslFactory = SSLFactory.builder()
                    .withTrustMaterial(trustStorePath, trustStorePassword)
                    .withHostnameVerifier(new DefaultHostnameVerifier())
                    .withProtocol("TLSv1.3")
                    .build();
        }

        if (twoWayAuthenticationEnabled) {
            sslFactory = SSLFactory.builder()
                    .withIdentityMaterial(keyStorePath, keyStorePassword)
                    .withTrustMaterial(trustStorePath, trustStorePassword)
                    .withHostnameVerifier(new DefaultHostnameVerifier())
                    .withProtocol("TLSv1.3")
                    .build();
        }

        return sslFactory;
    }

}
