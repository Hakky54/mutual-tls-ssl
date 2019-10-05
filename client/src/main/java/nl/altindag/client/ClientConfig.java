package nl.altindag.client;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SSLTrustManagerHelper.class})
public class ClientConfig {

    private SSLContext sslContext;

    public ClientConfig(@Autowired(required = false) SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Bean("httpClient")
    @ConditionalOnProperty(name = "client.ssl.mutual-authentication-enabled", havingValue = "true")
    public HttpClient httpClientWithMutualAuthentication() {
        return HttpClients.custom()
                .setSSLContext(sslContext)
                .build();
    }

    @Bean("httpClient")
    @ConditionalOnProperty(name = "client.ssl.mutual-authentication-enabled", havingValue = "false")
    public HttpClient httpClientWithoutMutualAuthentication() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return HttpClients.custom()
                .setSSLContext(SSLContexts.custom()
                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .build())
                .build();
    }

}
