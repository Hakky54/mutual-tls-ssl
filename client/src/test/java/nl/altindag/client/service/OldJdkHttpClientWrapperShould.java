package nl.altindag.client.service;

import static nl.altindag.client.TestConstants.HTTPS_URL;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import nl.altindag.client.ClientException;
import nl.altindag.client.SSLTrustManagerHelper;
import nl.altindag.client.model.ClientResponse;

@RunWith(MockitoJUnitRunner.class)
public class OldJdkHttpClientWrapperShould {

    private OldJdkHttpClientWrapper victim;
    private SSLTrustManagerHelper sslTrustManagerHelper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        sslTrustManagerHelper = mock(SSLTrustManagerHelper.class);

        victim = spy(new OldJdkHttpClientWrapper(sslTrustManagerHelper));
    }

    @Test
    public void executeHttpRequest() throws Exception {
        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());

        when(victim.createHttpURLConnection(HTTP_URL)).thenReturn(connection);
        when(connection.getInputStream()).thenReturn(stream);
        when(connection.getResponseCode()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

    @Test
    public void executeHttpsRequest() throws Exception {
        HttpsURLConnection connection = mock(HttpsURLConnection.class);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());
        SSLContext sslContext = mock(SSLContext.class);
        SSLSocketFactory sslSocketFactory = mock(SSLSocketFactory.class);

        when(victim.createHttpsURLConnection(HTTPS_URL)).thenReturn(connection);
        when(connection.getInputStream()).thenReturn(stream);
        when(connection.getResponseCode()).thenReturn(200);
        when(sslTrustManagerHelper.getSslContext()).thenReturn(sslContext);
        when(sslContext.getSocketFactory()).thenReturn(sslSocketFactory);

        ClientResponse clientResponse = victim.executeRequest(HTTPS_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(connection, times(1)).setSSLSocketFactory(sslSocketFactory);
    }

    @Test
    public void throwClientExceptionWhenProvidedUrlDoesNotContainHttpOrHttps() throws Exception {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Could not create a http client for one of these reasons: invalid url, security is enable while using an url with http or security is disable while using an url with https");

        victim.executeRequest("www.google.com");
    }

}
