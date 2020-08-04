package nl.altindag.server;

import nl.altindag.log.LogCaptor;
import nl.altindag.sslcontext.SSLFactory;
import nl.altindag.sslcontext.trustmanager.CompositeX509ExtendedTrustManager;
import nl.altindag.sslcontext.trustmanager.UnsafeX509ExtendedTrustManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class AppIT {

    private final LogCaptor logCaptor = LogCaptor.forClass(App.class);

    @BeforeEach
    void cleanUp() {
        logCaptor.clearLogs();
        App.stopServerIfRunning();
        System.clearProperty("properties");
    }

    @Test
    void startServerWithoutSecurity() throws IOException, InterruptedException {
        App.main(null);

        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/api/hello"))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertThat(httpResponse.body()).isEqualTo("Hello");
        assertThat(httpResponse.statusCode()).isEqualTo(200);
        assertThat(logCaptor.getInfoLogs()).containsExactly("Loading the following application properties: [application-without-authentication.properties]");

        cleanUp();
    }

    @Test
    void startServerWithOneWayAuthentication() throws IOException, InterruptedException {
        System.setProperty("properties", "application-one-way-authentication.properties");

        LogCaptor compositeTrustManagerLogCaptor = LogCaptor.forClass(CompositeX509ExtendedTrustManager.class);
        LogCaptor unsafeTrustManagerLogCaptor = LogCaptor.forClass(UnsafeX509ExtendedTrustManager.class);

        App.main(null);

        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
                .uri(URI.create("https://localhost:8443/api/hello"))
                .build();

        SSLFactory sslFactory = SSLFactory.builder()
                .withTrustingAllCertificatesWithoutValidation()
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslFactory.getSslContext())
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertThat(httpResponse.body()).isEqualTo("Hello");
        assertThat(httpResponse.statusCode()).isEqualTo(200);
        assertThat(logCaptor.getInfoLogs()).containsExactly("Loading the following application properties: [application-one-way-authentication.properties]");
        assertThat(compositeTrustManagerLogCaptor.getDebugLogs()).containsExactly("Received the following server certificate: [CN=Hakan, OU=Amsterdam, O=Thunderberry, C=NL]");
        assertThat(unsafeTrustManagerLogCaptor.getDebugLogs()).containsExactly("Accepting a server certificate: [CN=Hakan, OU=Amsterdam, O=Thunderberry, C=NL]");
    }

    @Test
    void startServerWithTwoWayAuthentication() throws IOException, InterruptedException {
        System.setProperty("properties", "application-two-way-authentication.properties");

        LogCaptor compositeTrustManagerLogCaptor = LogCaptor.forClass(CompositeX509ExtendedTrustManager.class);

        App.main(null);

        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
                .uri(URI.create("https://localhost:8443/api/hello"))
                .build();

        SSLFactory sslFactory = SSLFactory.builder()
                .withIdentityMaterial("client-identity.jks", "secret".toCharArray())
                .withTrustMaterial("client-truststore.jks", "secret".toCharArray())
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslFactory.getSslContext())
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertThat(httpResponse.body()).isEqualTo("Hello");
        assertThat(httpResponse.statusCode()).isEqualTo(200);
        assertThat(logCaptor.getInfoLogs()).containsExactly("Loading the following application properties: [application-two-way-authentication.properties]");
        assertThat(compositeTrustManagerLogCaptor.getDebugLogs())
                .containsExactly(
                        "Received the following server certificate: [CN=Hakan, OU=Amsterdam, O=Thunderberry, C=NL]",
                        "Received the following client certificate: [CN=black-hole, OU=Altindag, O=Altindag, C=NL]"
                );
    }

}
