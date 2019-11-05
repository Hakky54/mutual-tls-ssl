package nl.altindag.client;

import java.net.http.HttpClient;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({SSLTrustManagerHelper.class})
public class ClientConfig {

    private SSLContext sslContext;

    public ClientConfig(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Bean
    public CloseableHttpClient apacheHttpClient() {
        return HttpClients.custom()
                          .setSSLContext(sslContext)
                          .build();
    }

    @Bean
    public HttpClient jdkHttpClient() {
        return HttpClient.newBuilder()
                         .sslContext(sslContext)
                         .build();
    }

    @Bean
    public RestTemplate restTemplate(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return new RestTemplate(requestFactory);
    }

}
