package nl.altindag.client;

import java.net.http.HttpClient;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import io.netty.handler.ssl.SslContextBuilder;
import kong.unirest.Unirest;
import kong.unirest.apache.ApacheClient;
import okhttp3.OkHttpClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public SSLTrustManagerHelper sslTrustManagerHelper(@Value("${client.ssl.one-way-authentication-enabled:false}") boolean oneWayAuthenticationEnabled,
                                                       @Value("${client.ssl.two-way-authentication-enabled:false}") boolean twoWayAuthenticationEnabled,
                                                       @Value("${client.ssl.key-store:}") String keyStorePath,
                                                       @Value("${client.ssl.key-store-password:}") String keyStorePassword,
                                                       @Value("${client.ssl.trust-store:}") String trustStorePath,
                                                       @Value("${client.ssl.trust-store-password:}") String trustStorePassword) {
        return new SSLTrustManagerHelper(oneWayAuthenticationEnabled,
                                         twoWayAuthenticationEnabled,
                                         keyStorePath,
                                         keyStorePassword,
                                         trustStorePath,
                                         trustStorePassword);
    }

    @Bean
    @Scope("prototype")
    public org.apache.http.client.HttpClient apacheHttpClient(SSLTrustManagerHelper sslTrustManagerHelper) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            httpClientBuilder.setSSLContext(sslTrustManagerHelper.getSslContext());
            httpClientBuilder.setSSLHostnameVerifier(sslTrustManagerHelper.getDefaultHostnameVerifier());
        }
        return httpClientBuilder.build();
    }

    @Bean
    public HttpClient jdkHttpClient(SSLTrustManagerHelper sslTrustManagerHelper) {
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
    public OkHttpClient okHttpClient(SSLTrustManagerHelper sslTrustManagerHelper) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            httpClientBuilder.sslSocketFactory(sslTrustManagerHelper.getSslContext().getSocketFactory(), sslTrustManagerHelper.getX509TrustManager())
                             .hostnameVerifier(sslTrustManagerHelper.getDefaultHostnameVerifier());
        }

        return httpClientBuilder
                .build();
    }

    @Bean
    public WebClient webClientWithNetty(SSLTrustManagerHelper sslTrustManagerHelper) {
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
    public WebClient webClientWithJetty(SSLTrustManagerHelper sslTrustManagerHelper) {
        org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setSslContext(sslTrustManagerHelper.getSslContext());
            sslContextFactory.setHostnameVerifier(sslTrustManagerHelper.getDefaultHostnameVerifier());
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

    @Bean
    public Client jerseyClient(SSLTrustManagerHelper sslTrustManagerHelper) {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            clientBuilder.sslContext(sslTrustManagerHelper.getSslContext());
            clientBuilder.hostnameVerifier(sslTrustManagerHelper.getDefaultHostnameVerifier());
        }
        return clientBuilder.build();
    }

    @Bean
    public com.sun.jersey.api.client.Client oldJerseyClient(SSLTrustManagerHelper sslTrustManagerHelper) {
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslTrustManagerHelper.getSslContext().getSocketFactory());
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                                             new HTTPSProperties(sslTrustManagerHelper.getDefaultHostnameVerifier(), sslTrustManagerHelper.getSslContext()));
            com.sun.jersey.api.client.Client.create(clientConfig);
        }
        return client;
    }

    @Bean
    public HttpTransport googleHttpClient(SSLTrustManagerHelper sslTrustManagerHelper) {
        NetHttpTransport.Builder httpTransportBuilder = new NetHttpTransport.Builder();
        if (sslTrustManagerHelper.isSecurityEnabled()) {
            httpTransportBuilder.setSslSocketFactory(sslTrustManagerHelper.getSslContext().getSocketFactory())
                                .setHostnameVerifier(sslTrustManagerHelper.getDefaultHostnameVerifier());
        }
        return httpTransportBuilder
                .build();
    }

    @Autowired
    public void unirest(org.apache.http.client.HttpClient httpClient) {
        Unirest.primaryInstance()
               .config()
               .httpClient(config -> ApacheClient.builder(httpClient).apply(config));
    }

}
