package nl.altindag.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
@Import({SSLTrustManagerHelper.class})
public class ClientConfig {

    @Autowired(required = false)
    private SSLContext sslContext;

    @Bean("httpClient")
    @ConditionalOnProperty(name = "client.ssl.enabled", havingValue = "true")
    public HttpClient httpClientWithTLS() {
        return HttpClients.custom()
                .setSSLContext(sslContext)
                .build();
    }

    @Bean("httpClient")
    @ConditionalOnProperty(name = "client.ssl.enabled", havingValue = "false")
    public HttpClient httpClientWithoutTLS() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        return HttpClients.custom()
                .setSSLContext(SSLContexts.custom()
                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .build())
                .build();
    }

}
