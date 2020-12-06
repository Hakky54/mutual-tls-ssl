package nl.altindag.client;

import nl.altindag.sslcontext.SSLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
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
        SSLFactory sslFactory = null;

        if (oneWayAuthenticationEnabled) {
            sslFactory = SSLFactory.builder()
                    .withTrustMaterial(trustStorePath, trustStorePassword)
                    .withProtocols("TLSv1.3")
                    .build();
        }

        if (twoWayAuthenticationEnabled) {
            sslFactory = SSLFactory.builder()
                    .withIdentityMaterial(keyStorePath, keyStorePassword)
                    .withTrustMaterial(trustStorePath, trustStorePassword)
                    .withProtocols("TLSv1.3")
                    .withPasswordCaching() // <--- this option is only required for the ktor http client with cio engine
                    .build();
        }

        return sslFactory;
    }

}
