package nl.altindag.client;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.security.NoSuchAlgorithmException;

import javax.ws.rs.client.Client;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.api.client.http.HttpTransport;

import kong.unirest.Unirest;
import okhttp3.OkHttpClient;

@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(MockitoJUnitRunner.class)
public class ClientConfigShould {

    private ClientConfig victim = new ClientConfig();

    @Test
    public void createSslTrustManagerHelper() {
        SSLTrustManagerHelper sslTrustManagerHelper = victim.sslTrustManagerHelper(false, false, EMPTY, EMPTY, EMPTY, EMPTY);

        assertThat(sslTrustManagerHelper).isNotNull();
    }

    @Test
    public void createApacheHttpClientWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        HttpClient httpClient = victim.apacheHttpClient(sslTrustManagerHelper);

        assertThat(httpClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getDefaultHostnameVerifier();
    }

    @Test
    public void createApacheHttpClientWithSecurity() throws NoSuchAlgorithmException {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        HttpClient httpClient = victim.apacheHttpClient(sslTrustManagerHelper);

        assertThat(httpClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getDefaultHostnameVerifier();
    }

    @Test
    public void createJdkHttpClientWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        java.net.http.HttpClient httpClient = victim.jdkHttpClient(sslTrustManagerHelper);

        assertThat(httpClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
    }

    @Test
    public void createJdkHttpClientWithSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        java.net.http.HttpClient httpClient = victim.jdkHttpClient(sslTrustManagerHelper);

        assertThat(httpClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
    }

    @Test
    public void createRestTemplate() {
        HttpClient httpClient = mock(HttpClient.class);

        RestTemplate restTemplate = victim.restTemplate(httpClient);

        assertThat(restTemplate).isNotNull();
    }

    @Test
    public void createOkHttpClientWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        OkHttpClient okHttpClient = victim.okHttpClient(sslTrustManagerHelper);

        assertThat(okHttpClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getX509TrustManager();
        verify(sslTrustManagerHelper, times(0)).getDefaultHostnameVerifier();
    }

    @Test
    public void createOkHttpClientWithSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        OkHttpClient okHttpClient = victim.okHttpClient(sslTrustManagerHelper);

        assertThat(okHttpClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getX509TrustManager();
        verify(sslTrustManagerHelper, times(1)).getDefaultHostnameVerifier();

        assertThat(okHttpClient.hostnameVerifier()).isEqualTo(sslTrustManagerHelper.getDefaultHostnameVerifier());
        assertThat(okHttpClient.x509TrustManager()).isEqualTo(sslTrustManagerHelper.getX509TrustManager());
    }

    @Test
    public void createWebClientWithNettyWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        WebClient webClient = victim.webClientWithNetty(sslTrustManagerHelper);

        assertThat(webClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).isOneWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(0)).isTwoWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getKeyManagerFactory();
        verify(sslTrustManagerHelper, times(0)).getTrustManagerFactory();
    }

    @Test
    public void createWebClientWithNettyWithTwoWayAuthentication() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        WebClient webClient = victim.webClientWithNetty(sslTrustManagerHelper);

        assertThat(webClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).isOneWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).isTwoWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getKeyManagerFactory();
        verify(sslTrustManagerHelper, times(1)).getTrustManagerFactory();
    }

    @Test
    public void createWebClientWithNettyWithOneWayAuthentication() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(true, false);

        WebClient webClient = victim.webClientWithNetty(sslTrustManagerHelper);

        assertThat(webClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).isOneWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).isTwoWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getKeyManagerFactory();
        verify(sslTrustManagerHelper, times(1)).getTrustManagerFactory();
    }

    @Test
    public void createWebClientWithJettyWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        WebClient webClient = victim.webClientWithJetty(sslTrustManagerHelper);

        assertThat(webClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).isOneWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(0)).isTwoWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getDefaultHostnameVerifier();
        verify(sslTrustManagerHelper, times(0)).getTrustStore();
        verify(sslTrustManagerHelper, times(0)).getTrustStorePassword();
        verify(sslTrustManagerHelper, times(0)).getKeyStore();
        verify(sslTrustManagerHelper, times(0)).getKeyStorePassword();
    }

    @Test
    public void createWebClientWithJettyWithTwoWayAuthentication() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        WebClient webClient = victim.webClientWithJetty(sslTrustManagerHelper);

        assertThat(webClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).isOneWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).isTwoWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getDefaultHostnameVerifier();
        verify(sslTrustManagerHelper, times(1)).getTrustStore();
        verify(sslTrustManagerHelper, times(1)).getTrustStorePassword();
        verify(sslTrustManagerHelper, times(1)).getKeyStore();
        verify(sslTrustManagerHelper, times(1)).getKeyStorePassword();
    }

    @Test
    public void createWebClientWithJettyWithOneWayAuthentication() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(true,false);

        WebClient webClient = victim.webClientWithJetty(sslTrustManagerHelper);

        assertThat(webClient).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).isOneWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).isTwoWayAuthenticationEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getDefaultHostnameVerifier();
        verify(sslTrustManagerHelper, times(1)).getTrustStore();
        verify(sslTrustManagerHelper, times(1)).getTrustStorePassword();
        verify(sslTrustManagerHelper, times(0)).getKeyStore();
        verify(sslTrustManagerHelper, times(0)).getKeyStorePassword();
    }

    @Test
    public void createJerseyClientWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        Client client = victim.jerseyClient(sslTrustManagerHelper);

        assertThat(client).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getDefaultHostnameVerifier();
    }

    @Test
    public void createJerseyClientWithSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        Client client = victim.jerseyClient(sslTrustManagerHelper);

        assertThat(client).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getDefaultHostnameVerifier();
    }

    @Test
    public void createOldJerseyClientWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        com.sun.jersey.api.client.Client client = victim.oldJerseyClient(sslTrustManagerHelper);

        assertThat(client).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getDefaultHostnameVerifier();
    }

    @Test
    public void createOldJerseyClientWithSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        com.sun.jersey.api.client.Client client = victim.oldJerseyClient(sslTrustManagerHelper);

        assertThat(client).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(2)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getDefaultHostnameVerifier();
    }

    @Test
    public void createGoogleHttpClientWithoutSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, false);

        HttpTransport httpTransport = victim.googleHttpClient(sslTrustManagerHelper);

        assertThat(httpTransport).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(0)).getSslContext();
        verify(sslTrustManagerHelper, times(0)).getDefaultHostnameVerifier();
    }

    @Test
    public void createGoogleHttpClientWithSecurity() {
        SSLTrustManagerHelper sslTrustManagerHelper = createSSLTrustManagerHelper(false, true);

        HttpTransport httpTransport = victim.googleHttpClient(sslTrustManagerHelper);

        assertThat(httpTransport).isNotNull();
        verify(sslTrustManagerHelper, times(1)).isSecurityEnabled();
        verify(sslTrustManagerHelper, times(1)).getSslContext();
        verify(sslTrustManagerHelper, times(1)).getDefaultHostnameVerifier();
    }

    @Test
    public void createUnirestWithProvidedApacheHttpClient() {
        HttpClient httpClient = HttpClients.createDefault();

        victim.unirest(httpClient);

        Object client = Unirest.primaryInstance().config().getClient().getClient();
        assertThat(client).isEqualTo(httpClient);
    }

    private SSLTrustManagerHelper createSSLTrustManagerHelper(boolean oneWayAuthenticationEnabled, boolean twoWayAuthenticationEnabled) {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLTrustManagerHelper sslTrustManagerHelper = new SSLTrustManagerHelper(oneWayAuthenticationEnabled, twoWayAuthenticationEnabled, keyStorePath,
                                                                                keyStorePassword, trustStorePath, trustStorePassword);
        return Mockito.spy(sslTrustManagerHelper);
    }

}
