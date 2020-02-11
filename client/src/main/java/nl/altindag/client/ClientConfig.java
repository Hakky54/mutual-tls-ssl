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
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectionContext;
import akka.http.javadsl.HttpsConnectionContext;
import io.netty.handler.ssl.SslContextBuilder;
import kong.unirest.Unirest;
import nl.altindag.sslcontext.SSLFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public SSLFactory sslFactory(@Value("${client.ssl.one-way-authentication-enabled:false}") boolean oneWayAuthenticationEnabled,
                                 @Value("${client.ssl.two-way-authentication-enabled:false}") boolean twoWayAuthenticationEnabled,
                                 @Value("${client.ssl.key-store:}") String keyStorePath,
                                 @Value("${client.ssl.key-store-password:}") char[] keyStorePassword,
                                 @Value("${client.ssl.trust-store:}") String trustStorePath,
                                 @Value("${client.ssl.trust-store-password:}") char[] trustStorePassword) {
        SSLFactory.Builder sslFactoryBuilder = SSLFactory.builder();
        if (oneWayAuthenticationEnabled) {
            sslFactoryBuilder.withTrustStore(trustStorePath, trustStorePassword)
                             .withHostnameVerifierEnabled(true);
        }

        if (twoWayAuthenticationEnabled) {
            sslFactoryBuilder.withIdentity(keyStorePath, keyStorePassword)
                             .withTrustStore(trustStorePath, trustStorePassword)
                             .withHostnameVerifierEnabled(true);
        }
        return sslFactoryBuilder.build();
    }

    @Bean
    @Scope("prototype")
    public org.apache.http.client.HttpClient apacheHttpClient(SSLFactory sslFactory) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (sslFactory.isSecurityEnabled()) {
            httpClientBuilder.setSSLContext(sslFactory.getSslContext());
            httpClientBuilder.setSSLHostnameVerifier(sslFactory.getHostnameVerifier());
        }
        return httpClientBuilder.build();
    }

    @Bean
    public HttpClient jdkHttpClient(SSLFactory sslFactory) {
        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        if (sslFactory.isSecurityEnabled()) {
            httpClientBuilder.sslContext(sslFactory.getSslContext());
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
    public OkHttpClient okHttpClient(SSLFactory sslFactory) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (sslFactory.isSecurityEnabled()) {
            httpClientBuilder.sslSocketFactory(sslFactory.getSslContext().getSocketFactory(), sslFactory.getTrustManager())
                             .hostnameVerifier(sslFactory.getHostnameVerifier());
        }

        return httpClientBuilder
                .build();
    }

    @Bean
    public WebClient webClientWithNetty(SSLFactory sslFactory) {
        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create();
        if (sslFactory.isSecurityEnabled()) {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient()
                                                                   .startTls(true)
                                                                   .protocols(sslFactory.getSslContext().getProtocol());
            if (sslFactory.isOneWayAuthenticationEnabled()) {
                sslContextBuilder.trustManager(sslFactory.getTrustManagerFactory());
            }

            if (sslFactory.isTwoWayAuthenticationEnabled()) {
                sslContextBuilder.keyManager(sslFactory.getKeyManagerFactory())
                                 .trustManager(sslFactory.getTrustManagerFactory());
            }
            httpClient = httpClient.secure(sslSpec -> sslSpec.sslContext(sslContextBuilder));
        }

        return WebClient.builder()
                 .clientConnector(new ReactorClientHttpConnector(httpClient))
                 .build();
    }

    @Bean
    public WebClient webClientWithJetty(SSLFactory sslFactory) {
        org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient();
        if (sslFactory.isSecurityEnabled()) {
            SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
            sslContextFactory.setSslContext(sslFactory.getSslContext());
            sslContextFactory.setHostnameVerifier(sslFactory.getHostnameVerifier());
            httpClient = new org.eclipse.jetty.client.HttpClient(sslContextFactory);
        }

        return WebClient.builder()
                .clientConnector(new JettyClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public Client jerseyClient(SSLFactory sslFactory) {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        if (sslFactory.isSecurityEnabled()) {
            clientBuilder.sslContext(sslFactory.getSslContext());
            clientBuilder.hostnameVerifier(sslFactory.getHostnameVerifier());
        }
        return clientBuilder.build();
    }

    @Bean
    public com.sun.jersey.api.client.Client oldJerseyClient(SSLFactory sslFactory) {
        com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
        if (sslFactory.isSecurityEnabled()) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslFactory.getSslContext().getSocketFactory());
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
                                             new HTTPSProperties(sslFactory.getHostnameVerifier(), sslFactory.getSslContext()));
            com.sun.jersey.api.client.Client.create(clientConfig);
        }
        return client;
    }

    @Bean
    public HttpTransport googleHttpClient(SSLFactory sslFactory) {
        NetHttpTransport.Builder httpTransportBuilder = new NetHttpTransport.Builder();
        if (sslFactory.isSecurityEnabled()) {
            httpTransportBuilder.setSslSocketFactory(sslFactory.getSslContext().getSocketFactory())
                                .setHostnameVerifier(sslFactory.getHostnameVerifier());
        }
        return httpTransportBuilder
                .build();
    }

    @Autowired
    public void unirest(SSLFactory sslFactory) {
        if (sslFactory.isSecurityEnabled()) {
            Unirest.primaryInstance()
                   .config()
                   .sslContext(sslFactory.getSslContext())
                   .hostnameVerifier(sslFactory.getHostnameVerifier());
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
    public Service<Request, Response> finagle(SSLFactory sslFactory) throws URISyntaxException {
        URI uri = new URI(SERVER_URL);
        Http.Client client = Http.client();
        if (sslFactory.isSecurityEnabled()) {
            client = client
                    .withTransport()
                    .tls(sslFactory.getSslContext());
        }
        return client.newService(uri.getHost() + ":" + uri.getPort());
    }

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create(
                ClientConfig.class.getSimpleName(),
                ConfigFactory.defaultApplication(ClientConfig.class.getClassLoader())
        );
    }

    @Bean
    public akka.http.javadsl.Http akkaHttpClient(SSLFactory sslFactory, ActorSystem actorSystem) {
        akka.http.javadsl.Http http = akka.http.javadsl.Http.get(actorSystem);

        if (sslFactory.isSecurityEnabled()) {
            HttpsConnectionContext httpsContext = ConnectionContext.https(sslFactory.getSslContext());
            http.setDefaultClientHttpsContext(httpsContext);
        }
        return http;
    }

}
