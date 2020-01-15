package nl.altindag.client;

import static nl.altindag.client.util.AssertJCustomConditions.GSON_CONVERTER_FACTORY;
import static nl.altindag.client.util.AssertJCustomConditions.SUBSTRING_OF_HTTP_OR_HTTPS_SERVER_URL;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.client.Client;

import org.apache.http.client.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.http.HttpTransport;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;

import akka.http.javadsl.Http;
import kong.unirest.Unirest;
import nl.altindag.sslcontext.SSLContextHelper;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(MockitoJUnitRunner.class)
public class ClientConfigShould {

    private ClientConfig victim = new ClientConfig();

    @Test
    public void createSslTrustManagerHelper() {
        SSLContextHelper sslContextHelper = victim.sslTrustManagerHelper(false, false,
                                                                         EMPTY, EMPTY.toCharArray(), EMPTY, EMPTY.toCharArray());

        assertThat(sslContextHelper).isNotNull();
        assertThat(sslContextHelper.isSecurityEnabled()).isFalse();
        assertThat(sslContextHelper.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslContextHelper.isTwoWayAuthenticationEnabled()).isFalse();
    }

    @Test
    public void createSslTrustManagerHelperWithOneWayAuthentication() {
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLContextHelper sslContextHelper = victim.sslTrustManagerHelper(true, false,
                                                                         EMPTY, EMPTY.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslContextHelper).isNotNull();
        assertThat(sslContextHelper.isSecurityEnabled()).isTrue();
        assertThat(sslContextHelper.isOneWayAuthenticationEnabled()).isTrue();
        assertThat(sslContextHelper.isTwoWayAuthenticationEnabled()).isFalse();
    }

    @Test
    public void createSslTrustManagerHelperWithTwoWayAuthentication() {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLContextHelper sslContextHelper = victim.sslTrustManagerHelper(false, true,
                                                                         keyStorePath, keyStorePassword.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslContextHelper).isNotNull();
        assertThat(sslContextHelper.isSecurityEnabled()).isTrue();
        assertThat(sslContextHelper.isOneWayAuthenticationEnabled()).isFalse();
        assertThat(sslContextHelper.isTwoWayAuthenticationEnabled()).isTrue();
    }

    @Test
    public void createApacheHttpClientWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        HttpClient httpClient = victim.apacheHttpClient(sslContextHelper);

        assertThat(httpClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getHostnameVerifier();
    }

    @Test
    public void createApacheHttpClientWithSecurity() throws NoSuchAlgorithmException {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        HttpClient httpClient = victim.apacheHttpClient(sslContextHelper);

        assertThat(httpClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getHostnameVerifier();
    }

    @Test
    public void createJdkHttpClientWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        java.net.http.HttpClient httpClient = victim.jdkHttpClient(sslContextHelper);

        assertThat(httpClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
    }

    @Test
    public void createJdkHttpClientWithSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        java.net.http.HttpClient httpClient = victim.jdkHttpClient(sslContextHelper);

        assertThat(httpClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
    }

    @Test
    public void createRestTemplate() {
        HttpClient httpClient = mock(HttpClient.class);

        RestTemplate restTemplate = victim.restTemplate(httpClient);

        assertThat(restTemplate).isNotNull();
    }

    @Test
    public void createOkHttpClientWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        OkHttpClient okHttpClient = victim.okHttpClient(sslContextHelper);

        assertThat(okHttpClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getX509TrustManager();
        verify(sslContextHelper, times(0)).getHostnameVerifier();
    }

    @Test
    public void createOkHttpClientWithSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        OkHttpClient okHttpClient = victim.okHttpClient(sslContextHelper);

        assertThat(okHttpClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getX509TrustManager();
        verify(sslContextHelper, times(1)).getHostnameVerifier();

        assertThat(okHttpClient.hostnameVerifier()).isEqualTo(sslContextHelper.getHostnameVerifier());
        assertThat(okHttpClient.x509TrustManager()).isEqualTo(sslContextHelper.getX509TrustManager());
    }

    @Test
    public void createWebClientWithNettyWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        WebClient webClient = victim.webClientWithNetty(sslContextHelper);

        assertThat(webClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).isOneWayAuthenticationEnabled();
        verify(sslContextHelper, times(0)).isTwoWayAuthenticationEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getKeyManagerFactory();
        verify(sslContextHelper, times(0)).getTrustManagerFactory();
    }

    @Test
    public void createWebClientWithNettyWithTwoWayAuthentication() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        WebClient webClient = victim.webClientWithNetty(sslContextHelper);

        assertThat(webClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).isOneWayAuthenticationEnabled();
        verify(sslContextHelper, times(1)).isTwoWayAuthenticationEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getKeyManagerFactory();
        verify(sslContextHelper, times(1)).getTrustManagerFactory();
    }

    @Test
    public void createWebClientWithNettyWithOneWayAuthentication() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(true, false);

        WebClient webClient = victim.webClientWithNetty(sslContextHelper);

        assertThat(webClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).isOneWayAuthenticationEnabled();
        verify(sslContextHelper, times(1)).isTwoWayAuthenticationEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(0)).getKeyManagerFactory();
        verify(sslContextHelper, times(1)).getTrustManagerFactory();
    }

    @Test
    public void createWebClientWithJettyWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        WebClient webClient = victim.webClientWithJetty(sslContextHelper);

        assertThat(webClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).isOneWayAuthenticationEnabled();
        verify(sslContextHelper, times(0)).isTwoWayAuthenticationEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getHostnameVerifier();
        verify(sslContextHelper, times(0)).getTrustStore();
        verify(sslContextHelper, times(0)).getTrustStorePassword();
        verify(sslContextHelper, times(0)).getIdentity();
        verify(sslContextHelper, times(0)).getIdentityPassword();
    }

    @Test
    public void createWebClientWithJettyWithTwoWayAuthentication() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        WebClient webClient = victim.webClientWithJetty(sslContextHelper);

        assertThat(webClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).isOneWayAuthenticationEnabled();
        verify(sslContextHelper, times(0)).isTwoWayAuthenticationEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getHostnameVerifier();
        verify(sslContextHelper, times(0)).getTrustStore();
        verify(sslContextHelper, times(0)).getTrustStorePassword();
        verify(sslContextHelper, times(0)).getIdentity();
        verify(sslContextHelper, times(0)).getIdentityPassword();
    }

    @Test
    public void createWebClientWithJettyWithOneWayAuthentication() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(true, false);

        WebClient webClient = victim.webClientWithJetty(sslContextHelper);

        assertThat(webClient).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).isOneWayAuthenticationEnabled();
        verify(sslContextHelper, times(0)).isTwoWayAuthenticationEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getHostnameVerifier();
        verify(sslContextHelper, times(0)).getTrustStore();
        verify(sslContextHelper, times(0)).getTrustStorePassword();
        verify(sslContextHelper, times(0)).getIdentity();
        verify(sslContextHelper, times(0)).getIdentityPassword();
    }

    @Test
    public void createJerseyClientWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        Client client = victim.jerseyClient(sslContextHelper);

        assertThat(client).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getHostnameVerifier();

        client.close();
    }

    @Test
    public void createJerseyClientWithSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        Client client = victim.jerseyClient(sslContextHelper);

        assertThat(client).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getHostnameVerifier();

        client.close();
    }

    @Test
    public void createOldJerseyClientWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        com.sun.jersey.api.client.Client client = victim.oldJerseyClient(sslContextHelper);

        assertThat(client).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getHostnameVerifier();

        client.destroy();
    }

    @Test
    public void createOldJerseyClientWithSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        com.sun.jersey.api.client.Client client = victim.oldJerseyClient(sslContextHelper);

        assertThat(client).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(2)).getSslContext();
        verify(sslContextHelper, times(1)).getHostnameVerifier();

        client.destroy();
    }

    @Test
    public void createGoogleHttpClientWithoutSecurity() throws IOException {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        HttpTransport httpTransport = victim.googleHttpClient(sslContextHelper);

        assertThat(httpTransport).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getHostnameVerifier();

        httpTransport.shutdown();
    }

    @Test
    public void createGoogleHttpClientWithSecurity() throws IOException {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        HttpTransport httpTransport = victim.googleHttpClient(sslContextHelper);

        assertThat(httpTransport).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getHostnameVerifier();

        httpTransport.shutdown();
    }

    @Test
    public void createUnirestWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        victim.unirest(sslContextHelper);

        assertThat(Unirest.primaryInstance().config().getSslContext()).isNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
        verify(sslContextHelper, times(0)).getHostnameVerifier();

        Unirest.shutDown();
    }

    @Test
    public void createUnirestWithSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        victim.unirest(sslContextHelper);

        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
        verify(sslContextHelper, times(1)).getHostnameVerifier();

        assertThat(Unirest.primaryInstance().config().getSslContext()).isEqualTo(sslContextHelper.getSslContext());

        Unirest.shutDown();
    }

    @Test
    public void createRetrofitWithProvidedOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Retrofit retrofit = victim.retrofit(okHttpClient);

        assertThat(retrofit).isNotNull();
        assertThat(retrofit.baseUrl().toString()).has(SUBSTRING_OF_HTTP_OR_HTTPS_SERVER_URL);
        assertThat(retrofit.converterFactories()).has(GSON_CONVERTER_FACTORY);
    }

    @Test
    public void createFinagleClientWithoutSecurity() throws URISyntaxException {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        Service<Request, Response> service = victim.finagle(sslContextHelper);

        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();

        assertThat(service.isAvailable()).isTrue();
        assertThat(service.status().toString()).isEqualTo("Open");

        service.close();
    }

    @Test
    public void createFinagleClientWithSecurity() throws URISyntaxException {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        Service<Request, Response> service = victim.finagle(sslContextHelper);

        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();

        assertThat(service.isAvailable()).isTrue();
        assertThat(service.status().toString()).isEqualTo("Open");

        service.close();
    }

    @Test
    public void createAkkaHttpClientWithoutSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, false);

        Http http = victim.akkaHttpClient(sslContextHelper);

        assertThat(http).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(0)).getSslContext();
    }

    @Test
    public void createAkkaHttpClientWithSecurity() {
        SSLContextHelper sslContextHelper = createSSLContextHelper(false, true);

        Http http = victim.akkaHttpClient(sslContextHelper);

        assertThat(http).isNotNull();
        verify(sslContextHelper, times(1)).isSecurityEnabled();
        verify(sslContextHelper, times(1)).getSslContext();
    }

    private SSLContextHelper createSSLContextHelper(boolean oneWayAuthenticationEnabled, boolean twoWayAuthenticationEnabled) {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLContextHelper.Builder sslContextBuilder = SSLContextHelper.builder();
        if (oneWayAuthenticationEnabled) {
            sslContextBuilder.withTrustStore(trustStorePath, trustStorePassword.toCharArray())
                             .withHostnameVerifierEnabled(true);
        }

        if (twoWayAuthenticationEnabled) {
            sslContextBuilder.withIdentity(keyStorePath, keyStorePassword.toCharArray())
                             .withTrustStore(trustStorePath, trustStorePassword.toCharArray())
                             .withHostnameVerifierEnabled(true);
        }
        return Mockito.spy(sslContextBuilder.build());
    }

}
