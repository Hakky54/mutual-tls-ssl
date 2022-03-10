package nl.altindag.client;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import com.github.mizosoft.methanol.Methanol;
import com.google.api.client.http.HttpTransport;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import feign.Feign;
import jakarta.ws.rs.client.Client;
import kong.unirest.Unirest;
import nl.altindag.ssl.SSLFactory;
import okhttp3.OkHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.asynchttpclient.AsyncHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import retrofit2.Retrofit;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;

import static nl.altindag.client.util.AssertJCustomConditions.GSON_CONVERTER_FACTORY;
import static nl.altindag.client.util.AssertJCustomConditions.SUBSTRING_OF_HTTP_OR_HTTPS_SERVER_URL;
import static nl.altindag.client.util.SSLFactoryTestHelper.createSSLFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientConfigShould {

    private final ClientConfig victim = new ClientConfig();

    @Test
    void createApacheHttpClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        CloseableHttpClient httpClient = victim.apacheHttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getHostnameVerifier();
    }

    @Test
    void createApacheHttpAsyncClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        CloseableHttpAsyncClient httpClient = victim.apacheHttpAsyncClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getHostnameVerifier();
    }

    @Test
    void createApache5HttpClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = victim.apache5HttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getHostnameVerifier();
        verify(sslFactory, times(2)).getSslParameters();
    }

    @Test
    void createApache5HttpAsyncClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient httpClient = victim.apache5HttpAsyncClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
    }

    @Test
    void createJdkHttpClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        java.net.http.HttpClient httpClient = victim.jdkHttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getSslParameters();
    }

    @Test
    void createRestTemplate() {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);

        RestTemplate restTemplate = victim.restTemplate(httpClient);

        assertThat(restTemplate).isNotNull();
    }

    @Test
    void createOkHttpClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        OkHttpClient okHttpClient = victim.okHttpClient(sslFactory);

        assertThat(okHttpClient).isNotNull();
        verify(sslFactory, times(1)).getTrustManager();
        verify(sslFactory, times(1)).getHostnameVerifier();
        verify(sslFactory, times(1)).getSslSocketFactory();

        assertThat(sslFactory.getTrustManager()).isPresent();
        assertThat(okHttpClient.x509TrustManager()).isEqualTo(sslFactory.getTrustManager().get());
        assertThat(okHttpClient.hostnameVerifier()).isEqualTo(sslFactory.getHostnameVerifier());
    }

    @Test
    void createNettyHttpClientWithOneWayAuthentication() throws SSLException {
        SSLFactory sslFactory = createSSLFactory(true, false);

        reactor.netty.http.client.HttpClient httpClient = victim.nettyHttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getTrustManager();
        verify(sslFactory, times(1)).getCiphers();
        verify(sslFactory, times(1)).getProtocols();
    }

    @Test
    void createNettyHttpClientWithTwoWayAuthentication() throws SSLException {
        SSLFactory sslFactory = createSSLFactory(false, true);

        reactor.netty.http.client.HttpClient httpClient = victim.nettyHttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getKeyManager();
        verify(sslFactory, times(1)).getTrustManager();
        verify(sslFactory, times(1)).getCiphers();
        verify(sslFactory, times(1)).getProtocols();
    }

    @Test
    void createWebClientWithNetty() {
        reactor.netty.http.client.HttpClient httpClient = mock(reactor.netty.http.client.HttpClient.class);
        WebClient webClient = victim.webClientWithNetty(httpClient);

        assertThat(webClient).isNotNull();
    }

    @Test
    void createJettyHttpClientWithOneWayAuthentication() {
        SSLFactory sslFactory = createSSLFactory(true, false);

        org.eclipse.jetty.client.HttpClient httpClient = victim.jettyHttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getHostnameVerifier();
    }

    @Test
    void createJettyHttpClientWithTwoWayAuthentication() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        org.eclipse.jetty.client.HttpClient httpClient = victim.jettyHttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getHostnameVerifier();
    }

    @Test
    void createWebClientWithJetty() {
        org.eclipse.jetty.client.HttpClient httpClient = mock(org.eclipse.jetty.client.HttpClient.class);
        WebClient webClient = victim.webClientWithJetty(httpClient);

        assertThat(webClient).isNotNull();
    }

    @Test
    void createJerseyClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        Client client = victim.jerseyClient(sslFactory);

        assertThat(client).isNotNull();
        assertThat(client.getClass().getPackageName())
                .as("Jersey JAX-RS implemenatsion is used")
                .startsWith("org.glassfish.jersey");

        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getHostnameVerifier();

        client.close();
    }

    @Test
    void createOldJerseyClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        com.sun.jersey.api.client.Client client = victim.oldJerseyClient(sslFactory);

        assertThat(client).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getHostnameVerifier();

        client.destroy();
    }

    @Test
    void createCxfJaxRsClient() {
         SSLFactory sslFactory = createSSLFactory(false, true);

         javax.ws.rs.client.Client client = victim.cxfJaxRsClient(sslFactory);

         assertThat(client).isNotNull();
         verify(sslFactory, times(1)).getSslContext();
         verify(sslFactory, times(1)).getHostnameVerifier();

         assertThat(client.getSslContext()).isNotNull();
         assertThat(client.getHostnameVerifier()).isNotNull();


         client.close();
    }

    @Test
    void createCxfWebClientWithoutSecurity() {
        org.apache.cxf.jaxrs.client.WebClient client = victim.cxfWebClient(null);

         assertThat(client).isNotNull();

         client.close();
    }

    @Test
    void createCxfWebClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        org.apache.cxf.jaxrs.client.WebClient client = victim.cxfWebClient(sslFactory);

        assertThat(client).isNotNull();
        assertThatThrownBy(() -> client.to(TestConstants.HTTPS_URL, false).get())
                .hasRootCauseInstanceOf(ConnectException.class);

        verify(sslFactory, times(1)).getSslSocketFactory();
        verify(sslFactory, times(1)).getHostnameVerifier();

        client.close();
    }

    @Test
    void createGoogleHttpClient() throws IOException {
        SSLFactory sslFactory = createSSLFactory(false, true);

        HttpTransport httpTransport = victim.googleHttpClient(sslFactory);

        assertThat(httpTransport).isNotNull();
        verify(sslFactory, times(1)).getSslSocketFactory();
        verify(sslFactory, times(1)).getHostnameVerifier();

        httpTransport.shutdown();
    }

    @Test
    void createUnirest() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        victim.unirest(sslFactory);

        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(2)).getSslParameters();
        verify(sslFactory, times(1)).getHostnameVerifier();

        assertThat(Unirest.primaryInstance().config().getSslContext()).isEqualTo(sslFactory.getSslContext());

        Unirest.shutDown();
    }

    @Test
    void createRetrofitWithProvidedOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Retrofit retrofit = victim.retrofit(okHttpClient);

        assertThat(retrofit).isNotNull();
        assertThat(retrofit.baseUrl().toString()).has(SUBSTRING_OF_HTTP_OR_HTTPS_SERVER_URL);
        assertThat(retrofit.converterFactories()).has(GSON_CONVERTER_FACTORY);
    }

    @Test
    void createFinagleClientWithoutSecurity() throws URISyntaxException {
        System.setProperty("url", TestConstants.HTTP_URL);
        Service<Request, Response> service = victim.finagle(null);

        assertThat(service.isAvailable()).isTrue();
        assertThat(service.status()).hasToString("Open");

        service.close();
        System.clearProperty("url");
    }

    @Test
    void createFinagleClientWithSecurity() throws URISyntaxException {
        System.setProperty("url", TestConstants.HTTPS_URL);
        SSLFactory sslFactory = createSSLFactory(false, true);

        Service<Request, Response> service = victim.finagle(sslFactory);

        verify(sslFactory, times(1)).getSslContext();

        assertThat(service.isAvailable()).isTrue();
        assertThat(service.status()).hasToString("Open");

        service.close();
        System.clearProperty("url");
    }

    @Test
    void createAkkaHttpClient() {
        SSLFactory sslFactory = createSSLFactory(false, true);

        Http http = victim.akkaHttpClient(sslFactory, ActorSystem.create());

        assertThat(http).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
    }

    @Test
    void createActorSystem() {
        ActorSystem actorSystem = victim.actorSystem();

        assertThat(actorSystem).isNotNull();
        assertThat(actorSystem.name()).isEqualTo("ClientConfig");
    }

    @Test
    void createAsyncHttpClient() throws SSLException {
        SSLFactory sslFactory = createSSLFactory(true, false);

        AsyncHttpClient httpClient = victim.asyncHttpClient(sslFactory);

        assertThat(httpClient).isNotNull();
        assertThat(httpClient.getConfig().getSslContext()).isNotNull();
        verify(sslFactory, times(1)).getTrustManager();
        verify(sslFactory, times(1)).getCiphers();
        verify(sslFactory, times(1)).getProtocols();
    }

    @Test
    void createFeign() {
        SSLFactory sslFactory = createSSLFactory(true, true);

        Feign.Builder feignBuilder = victim.feignWithOldJdkHttpClient(sslFactory);

        assertThat(feignBuilder).isNotNull();
        verify(sslFactory, times(1)).getSslSocketFactory();
        verify(sslFactory, times(1)).getHostnameVerifier();
    }

    @Test
    void createFeignWithOkHttpClient() {
        Feign.Builder feignBuilder = victim.feignWithOkHttpClient(mock(OkHttpClient.class));

        assertThat(feignBuilder).isNotNull();
    }

    @Test
    void createFeignWithApacheHttpClient() {
        Feign.Builder feignBuilder = victim.feignWithApacheHttpClient(mock(org.apache.http.impl.client.CloseableHttpClient.class));

        assertThat(feignBuilder).isNotNull();
    }

    @Test
    void createFeignWithApache5HttpClient() {
        Feign.Builder feignBuilder = victim.feignWithApache5HttpClient(mock(org.apache.hc.client5.http.impl.classic.CloseableHttpClient.class));

        assertThat(feignBuilder).isNotNull();
    }

    @Test
    void createFeignWithGoogleHttpClient() {
        Feign.Builder feignBuilder = victim.feignWithGoogleHttpClient(mock(HttpTransport.class));

        assertThat(feignBuilder).isNotNull();
    }

    @Test
    void createFeignWithJdkHttpClient() {
        Feign.Builder feignBuilder = victim.feignWithJdkHttpClient(mock(HttpClient.class));

        assertThat(feignBuilder).isNotNull();
    }

    @Test
    void createMethanol() {
        SSLFactory sslFactory = createSSLFactory(true, true);

        Methanol httpClient = victim.methanol(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getSslContext();
        verify(sslFactory, times(1)).getSslParameters();
    }

    @Test
    void createVertxWithoutSecurity() {
        System.setProperty("url", TestConstants.HTTP_URL);

        io.vertx.ext.web.client.WebClient webClient = victim.vertxWebClient(null);

        assertThat(webClient).isNotNull();
        System.clearProperty("url");
    }

    @Test
    void createVertxWithSecurity() {
        System.setProperty("url", TestConstants.HTTPS_URL);
        SSLFactory sslFactory = createSSLFactory(true, true);

        io.vertx.ext.web.client.WebClient httpClient = victim.vertxWebClient(sslFactory);

        assertThat(httpClient).isNotNull();
        verify(sslFactory, times(1)).getKeyManager();
        verify(sslFactory, times(1)).getTrustManager();
        verify(sslFactory, times(1)).getCiphers();
        verify(sslFactory, times(1)).getProtocols();

        System.clearProperty("url");
    }

}
