package nl.altindag.client;

import java.net.http.HttpClient;

import javax.net.ssl.X509TrustManager;

import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
        return HttpClients.custom()
                          .setSSLContext(sslTrustManagerHelper.getSslContext())
                          .build();
    }

    @Bean
    public HttpClient jdkHttpClient() {
        return HttpClient.newBuilder()
                         .sslContext(sslTrustManagerHelper.getSslContext())
                         .build();
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
        if (sslTrustManagerHelper.getTrustManagerFactory().isPresent()) {
            httpClientBuilder.sslSocketFactory(sslTrustManagerHelper.getSslContext().getSocketFactory(),
                                               (X509TrustManager) sslTrustManagerHelper.getTrustManagerFactory().get().getTrustManagers()[0]);
        }

        return httpClientBuilder
                .build();
    }

    @Bean
    public WebClient webClientWithNetty() {
        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create();
        if (sslTrustManagerHelper.getKeyManagerFactory().isPresent() && sslTrustManagerHelper.getTrustManagerFactory().isPresent()) {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient()
                                                                   .keyManager(sslTrustManagerHelper.getKeyManagerFactory().get())
                                                                   .trustManager(sslTrustManagerHelper.getTrustManagerFactory().get());

            httpClient = httpClient.secure(sslSpec -> sslSpec.sslContext(sslContextBuilder));
        }

        return WebClient.builder()
                 .clientConnector(new ReactorClientHttpConnector(httpClient))
                 .build();
    }

}
