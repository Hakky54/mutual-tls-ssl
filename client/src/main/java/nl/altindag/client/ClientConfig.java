package nl.altindag.client;

import java.net.http.HttpClient;

import javax.net.ssl.X509TrustManager;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import okhttp3.OkHttpClient;

@Configuration
@Import({SSLTrustManagerHelper.class})
public class ClientConfig {

    private final SSLTrustManagerHelper sslTrustManagerHelper;

    public ClientConfig(SSLTrustManagerHelper sslTrustManagerHelper) {
        this.sslTrustManagerHelper = sslTrustManagerHelper;
    }

    @Bean
    public CloseableHttpClient apacheHttpClient() {
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
    public RestTemplate restTemplate(CloseableHttpClient httpClient) {
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

}
