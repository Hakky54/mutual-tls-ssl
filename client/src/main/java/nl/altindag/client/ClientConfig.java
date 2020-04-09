package nl.altindag.client;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectionContext;
import akka.http.javadsl.HttpsConnectionContext;
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
import io.netty.handler.ssl.SslContext;
import kong.unirest.Unirest;
import nl.altindag.sslcontext.SSLFactory;
import nl.altindag.sslcontext.util.JettySslContextUtils;
import nl.altindag.sslcontext.util.NettySslContextUtils;
import okhttp3.OkHttpClient;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.Optional;

import static nl.altindag.client.Constants.SERVER_URL;

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
                    .withHostnameVerifier(new DefaultHostnameVerifier())
                    .withProtocol("TLSv1.3");
        }

        if (twoWayAuthenticationEnabled) {
            sslFactoryBuilder.withIdentity(keyStorePath, keyStorePassword)
                    .withTrustStore(trustStorePath, trustStorePassword)
                    .withHostnameVerifier(new DefaultHostnameVerifier())
                    .withProtocol("TLSv1.3");
        }
        return sslFactoryBuilder.build();
    }

    @Bean
    @Scope("prototype")
    public org.apache.http.client.HttpClient apacheHttpClient(SSLFactory sslFactory) {
        if (sslFactory.isSecurityEnabled()) {
            return HttpClients.custom()
                    .setSSLContext(sslFactory.getSslContext())
                    .setSSLHostnameVerifier(sslFactory.getHostnameVerifier())
                    .build();
        } else {
            return HttpClients.createMinimal();
        }
    }

    @Bean
    public HttpClient jdkHttpClient(SSLFactory sslFactory) {
        if (sslFactory.isSecurityEnabled()) {
            return HttpClient.newBuilder()
                    .sslParameters(sslFactory.getSslContext().getDefaultSSLParameters())
                    .sslContext(sslFactory.getSslContext())
                    .build();
        } else {
            return HttpClient.newHttpClient();
        }
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
        if (sslFactory.isSecurityEnabled()) {
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslFactory.getSslContext().getSocketFactory(), sslFactory.getTrustManager())
                    .hostnameVerifier(sslFactory.getHostnameVerifier())
                    .build();
        } else {
            return new OkHttpClient();
        }
    }

    @Bean
    public WebClient webClientWithNetty(SSLFactory sslFactory) throws SSLException {
        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create();
        if (sslFactory.isSecurityEnabled()) {
            SslContext sslContext = NettySslContextUtils.forClient(sslFactory).build();

            httpClient = httpClient.secure(sslSpec -> sslSpec.sslContext(sslContext));
        }

        return WebClient.builder()
                 .clientConnector(new ReactorClientHttpConnector(httpClient))
                 .build();
    }

    @Bean
    public WebClient webClientWithJetty(SSLFactory sslFactory) {
        org.eclipse.jetty.client.HttpClient httpClient;
        if (sslFactory.isSecurityEnabled()) {
            SslContextFactory.Client sslContextFactory = JettySslContextUtils.forClient(sslFactory);

            httpClient = new org.eclipse.jetty.client.HttpClient(sslContextFactory);
        } else {
            httpClient = new org.eclipse.jetty.client.HttpClient();
        }

        return WebClient.builder()
                .clientConnector(new JettyClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public Client jerseyClient(SSLFactory sslFactory) {
        if (sslFactory.isSecurityEnabled()) {
            return ClientBuilder.newBuilder()
                    .sslContext(sslFactory.getSslContext())
                    .hostnameVerifier(sslFactory.getHostnameVerifier())
                    .build();
        } else {
            return ClientBuilder.newClient();
        }
    }

    @Bean
    public com.sun.jersey.api.client.Client oldJerseyClient(SSLFactory sslFactory) {
        if (sslFactory.isSecurityEnabled()) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslFactory.getSslContext().getSocketFactory());
            DefaultClientConfig clientConfig = new DefaultClientConfig();
            clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(sslFactory.getHostnameVerifier(), sslFactory.getSslContext()));
            return com.sun.jersey.api.client.Client.create(clientConfig);
        } else {
            return com.sun.jersey.api.client.Client.create();
        }
    }

    @Bean
    public HttpTransport googleHttpClient(SSLFactory sslFactory) {
        if (sslFactory.isSecurityEnabled()) {
            return new NetHttpTransport.Builder()
                    .setSslSocketFactory(sslFactory.getSslContext().getSocketFactory())
                    .setHostnameVerifier(sslFactory.getHostnameVerifier())
                    .build();
        } else {
            return new NetHttpTransport();
        }
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
            HttpsConnectionContext httpsContext = ConnectionContext.https(
                    sslFactory.getSslContext(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(sslFactory.getSslContext().getDefaultSSLParameters()));
            http.setDefaultClientHttpsContext(httpsContext);
        }
        return http;
    }

    @Bean
    public dispatch.Http dispatchRebootHttpClient(SSLFactory sslFactory) throws SSLException {
        if (sslFactory.isSecurityEnabled()) {
            SslContext sslContext = NettySslContextUtils.forClient(sslFactory).build();

            DefaultAsyncHttpClientConfig.Builder clientConfigBuilder = dispatch.Http.defaultClientBuilder()
                    .setSslContext(sslContext);

            return dispatch.Http.withConfiguration(defaultClientConfigBuilder -> clientConfigBuilder);
        } else {
            return dispatch.Http.withConfiguration(builder -> builder);
        }
    }

    @Bean
    public AsyncHttpClient asyncHttpClient(SSLFactory sslFactory) throws SSLException {
        if (sslFactory.isSecurityEnabled()) {
            SslContext sslContext = NettySslContextUtils.forClient(sslFactory).build();

            DefaultAsyncHttpClientConfig.Builder clientConfigBuilder = dispatch.Http.defaultClientBuilder()
                    .setSslContext(sslContext);

            return Dsl.asyncHttpClient(clientConfigBuilder);
        } else {
            return Dsl.asyncHttpClient();
        }
    }

}
