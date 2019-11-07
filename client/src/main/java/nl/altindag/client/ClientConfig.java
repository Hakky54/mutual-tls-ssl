package nl.altindag.client;

import java.net.http.HttpClient;

import javax.net.ssl.X509TrustManager;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContextBuilder;
import okhttp3.OkHttpClient;

@Configuration
@Import({SSLTrustManagerHelper.class})
public class ClientConfig {

    private final SSLTrustManagerHelper sslTrustManagerHelper;

    public ClientConfig(SSLTrustManagerHelper sslTrustManagerHelper) {
        this.sslTrustManagerHelper = sslTrustManagerHelper;
    }

    @Bean
    public org.apache.http.client.HttpClient apacheHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            httpClientBuilder.setSSLContext(sslTrustManagerHelper.getSslContext());
        }
        return httpClientBuilder.build();
    }

    @Bean
    public HttpClient jdkHttpClient() {
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            httpClientBuilder.sslContext(sslTrustManagerHelper.getSslContext());
        }
        return httpClientBuilder.build();
    }

    @Bean
    public RestTemplate restTemplate(org.apache.http.client.HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }

    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            httpClientBuilder.sslSocketFactory(sslTrustManagerHelper.getSslContext().getSocketFactory(),
                                               (X509TrustManager) sslTrustManagerHelper.getTrustManagerFactory().getTrustManagers()[0]);
        }

        return httpClientBuilder
                .build();
    }

    @Bean
    public WebClient webClientWithNetty() {
        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient()
                                                                   .startTls(true)
                                                                   .protocols(sslTrustManagerHelper.getSslContext().getProtocol());
            if (sslTrustManagerHelper.isOneWayAuthenticationEnabled()) {
                sslContextBuilder.trustManager(sslTrustManagerHelper.getTrustManagerFactory());
            }

            if (sslTrustManagerHelper.isTwoWayAuthenticationEnabled()) {
                sslContextBuilder.keyManager(sslTrustManagerHelper.getKeyManagerFactory())
                                 .trustManager(sslTrustManagerHelper.getTrustManagerFactory());
            }
            httpClient = httpClient.secure(sslSpec -> sslSpec.sslContext(sslContextBuilder));
        }

        return WebClient.builder()
                 .clientConnector(new ReactorClientHttpConnector(httpClient))
                 .build();
    }

    @Bean
    public WebClient webClientWithJetty() {
        org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setSslContext(sslContextFactory.getSslContext());
            if (sslTrustManagerHelper.isOneWayAuthenticationEnabled()) {
                sslContextFactory.setTrustStore(sslTrustManagerHelper.getTrustStore());
                sslContextFactory.setTrustStorePassword(sslTrustManagerHelper.getTrustStorePassword());
            }

            if (sslTrustManagerHelper.isTwoWayAuthenticationEnabled()) {
                sslContextFactory.setKeyStore(sslTrustManagerHelper.getKeyStore());
                sslContextFactory.setKeyStorePassword(sslTrustManagerHelper.getKeyStorePassword());
                sslContextFactory.setTrustStore(sslTrustManagerHelper.getTrustStore());
                sslContextFactory.setTrustStorePassword(sslTrustManagerHelper.getTrustStorePassword());
            }
            httpClient = new org.eclipse.jetty.client.HttpClient(sslContextFactory);
        }

        return WebClient.builder()
                .clientConnector(new JettyClientHttpConnector(httpClient))
                .build();
    }

}
