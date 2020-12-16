package nl.altindag.client.service;

import static nl.altindag.client.ClientType.OLD_JDK_HTTP_CLIENT;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTPS_URL;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import nl.altindag.client.ClientException;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.ssl.SSLFactory;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OldJdkHttpClientServiceShould {

    @Test
    void executeHttpRequest() throws Exception {
        OldJdkHttpClientService victim = spy(new OldJdkHttpClientService(null));

        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());

        when(victim.createHttpURLConnection(HTTP_URL)).thenReturn(connection);
        when(connection.getInputStream()).thenReturn(stream);
        when(connection.getResponseCode()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(connection, times(1)).setRequestProperty(HEADER_KEY_CLIENT_TYPE, OLD_JDK_HTTP_CLIENT.getValue());
    }

    @Test
    void executeHttpsRequest() throws Exception {
        SSLFactory sslFactory = mock(SSLFactory.class);
        OldJdkHttpClientService victim = spy(new OldJdkHttpClientService(sslFactory));

        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());
        SSLSocketFactory sslSocketFactory = mock(SSLSocketFactory.class);

        when(victim.createHttpsURLConnection(HTTPS_URL)).thenReturn(connection);
        when(connection.getInputStream()).thenReturn(stream);
        when(connection.getResponseCode()).thenReturn(200);
        when(sslFactory.getSslSocketFactory()).thenReturn(sslSocketFactory);

        ClientResponse clientResponse = victim.executeRequest(HTTPS_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(connection, times(1)).setSSLSocketFactory(sslSocketFactory);
        verify(connection, times(1)).setHostnameVerifier(sslFactory.getHostnameVerifier());
        verify(connection, times(1)).setRequestProperty(HEADER_KEY_CLIENT_TYPE, OLD_JDK_HTTP_CLIENT.getValue());
    }

    @Test
    void throwClientExceptionWhenProvidedUrlDoesNotContainHttpOrHttps() {
        SSLFactory sslFactory = mock(SSLFactory.class);
        OldJdkHttpClientService victim = spy(new OldJdkHttpClientService(sslFactory));

        assertThatThrownBy(() -> victim.executeRequest("www.google.com"))
                .isInstanceOf(ClientException.class)
                .hasMessage("Could not create a http client for one of these reasons: invalid url, security is enable while using an url with http or security is disable while using an url with https");
    }

}
