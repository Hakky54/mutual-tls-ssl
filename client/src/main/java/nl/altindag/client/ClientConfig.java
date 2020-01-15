package nl.altindag.client;

import static nl.altindag.client.Constants.SERVER_URL;

import java.net.URI;
import java.net.URISyntaxException;
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
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.twitter.finagle.Http;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;

import akka.actor.ActorSystem;
import akka.http.javadsl.HttpsConnectionContext;
import io.netty.handler.ssl.SslContextBuilder;
import kong.unirest.Unirest;
import nl.altindag.sslcontext.SSLContextHelper;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public SSLContextHelper sslTrustManagerHelper(@Value("${client.ssl.one-way-authentication-enabled:false}") boolean oneWayAuthenticationEnabled,
                                                  @Value("${client.ssl.two-way-authentication-enabled:false}") boolean twoWayAuthenticationEnabled,
                                                  @Value("${client.ssl.key-store:}") String keyStorePath,
                                                  @Value("${client.ssl.key-store-password:}") String keyStorePassword,
                                                  @Value("${client.ssl.trust-store:}") String trustStorePath,
                                                  @Value("${client.ssl.trust-store-password:}") String trustStorePassword) {
        SSLContextHelper.Builder sslContextHelperBuilder = SSLContextHelper.builder();
        if (oneWayAuthenticationEnabled) {
            sslContextHelperBuilder.withTrustStore(trustStorePath, trustStorePassword)
                                   .withHostnameVerifierEnabled(true);
        }

        if (twoWayAuthenticationEnabled) {
            sslContextHelperBuilder.withIdentity(keyStorePath, keyStorePassword)
                                   .withTrustStore(trustStorePath, trustStorePassword)
                                   .withHostnameVerifierEnabled(true);
        }
        return sslContextHelperBuilder.build();
    }

    @Bean
    @Scope("prototype")
    public org.apache.http.client.HttpClient apacheHttpClient(SSLContextHelper sslContextHelper) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (sslContextHelper.isSecurityEnabled()) {
            httpClientBuilder.setSSLContext(sslContextHelper.getSslContext());
            httpClientBuilder.setSSLHostnameVerifier(sslContextHelper.getHostnameVerifier());
        }
        return httpClientBuilder.build();
    }

    @Bean
    public HttpClient jdkHttpClient(SSLContextHelper sslContextHelper) {
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        if (sslContextHelper.isSecurityEnabled()) {
            httpClientBuilder.sslContext(sslContextHelper.getSslContext());
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
    @Scope("prototype")
    public OkHttpClient okHttpClient(SSLContextHelper sslContextHelper) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (sslContextHelper.isSecurityEnabled()) {
            httpClientBuilder.sslSocketFactory(sslContextHelper.getSslContext().getSocketFactory(), sslContextHelper.getX509TrustManager())
                             .hostnameVerifier(sslContextHelper.getHostnameVerifier());
        }

        return httpClientBuilder
                .build();
    }

    @Bean
    public WebClient webClientWithNetty(SSLContextHelper sslContextHelper) {
        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create();
        if (sslContextHelper.isSecurityEnabled()) {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient()
                                                                   .startTls(true)
                                                                   .protocols(sslContextHelper.getSslContext().getProtocol());
            if (sslContextHelper.isOneWayAuthenticationEnabled()) {
                sslContextBuilder.trustManager(sslContextHelper.getTrustManagerFactory());
            }

            if (sslContextHelper.isTwoWayAuthenticationEnabled()) {
                sslContextBuilder.keyManager(sslContextHelper.getKeyManagerFactory())
                                 .trustManager(sslContextHelper.getTrustManagerFactory());
            }
            httpClient = httpClient.secure(sslSpec -> sslSpec.sslContext(sslContextBuilder));
        }

        return WebClient.builder()
                 .clientConnector(new ReactorClientHttpConnector(httpClient))
                 .build();
    }

    @Bean
    public WebClient webClientWithJetty(SSLContextHelper sslContextHelper) {
        org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient();
        if (sslContextHelper.isSecurityEnabled()) {
            SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
            sslContextFactory.setSslContext(sslContextHelper.getSslContext());
            sslContextFactory.setHostnameVerifier(sslContextHelper.getHostnameVerifier());
            httpClient = new org.eclipse.jetty.client.HttpClient(sslContextFactory);
        }

        return WebClient.builder()
                .clientConnector(new JettyClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public Client jerseyClient(SSLContextHelper sslContextHelper) {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        if (sslContextHelper.isSecurityEnabled()) {
            clientBuilder.sslContext(sslContextHelper.getSslContext());
            clientBuilder.hostnameVerifier(sslContextHelper.getHostnameVerifier());
        }
        return clientBuilder.build();
    }

    @Bean
    public com.sun.jersey.api.client.Client oldJerseyClient(SSLContextHelper sslContextHelper) {
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
        if (sslContextHelper.isSecurityEnabled()) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContextHelper.getSslContext().getSocketFactory());
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                                             new HTTPSProperties(sslContextHelper.getHostnameVerifier(), sslContextHelper.getSslContext()));
            com.sun.jersey.api.client.Client.create(clientConfig);
        }
        return client;
    }

    @Bean
    public HttpTransport googleHttpClient(SSLContextHelper sslContextHelper) {
        NetHttpTransport.Builder httpTransportBuilder = new NetHttpTransport.Builder();
        if (sslContextHelper.isSecurityEnabled()) {
            httpTransportBuilder.setSslSocketFactory(sslContextHelper.getSslContext().getSocketFactory())
                                .setHostnameVerifier(sslContextHelper.getHostnameVerifier());
        }
        return httpTransportBuilder
                .build();
    }

    @Autowired
    public void unirest(SSLContextHelper sslContextHelper) {
        if (sslContextHelper.isSecurityEnabled()) {
            Unirest.primaryInstance()
                   .config()
                   .sslContext(sslContextHelper.getSslContext())
                   .hostnameVerifier(sslContextHelper.getHostnameVerifier());
        }
    }

    @Bean
    public Retrofit retrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
    }

    @Bean
    public Service<Request, Response> finagle(SSLContextHelper sslContextHelper) throws URISyntaxException {
        URI uri = new URI(SERVER_URL);
        Http.Client client = Http.client();
        if (sslContextHelper.isSecurityEnabled()) {
            client = client
                    .withTransport()
                    .tls(sslContextHelper.getSslContext());
        }
        return client.newService(uri.getHost() + ":" + uri.getPort());
    }

    @Bean
    public akka.http.javadsl.Http akkaHttpClient(SSLContextHelper sslContextHelper) {
        ActorSystem system = ActorSystem.create();
        akka.http.javadsl.Http http = akka.http.javadsl.Http.get(system);

        if (sslContextHelper.isSecurityEnabled()) {
            HttpsConnectionContext httpsContext = HttpsConnectionContext.https(sslContextHelper.getSslContext());
            http.setDefaultClientHttpsContext(httpsContext);
        }
        return http;
    }

}
