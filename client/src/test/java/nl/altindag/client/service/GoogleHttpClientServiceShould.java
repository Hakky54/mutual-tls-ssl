package nl.altindag.client.service;

import static nl.altindag.client.ClientType.GOOGLE_HTTP_CLIENT;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

import nl.altindag.client.model.ClientResponse;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleHttpClientServiceShould {

    @InjectMocks
    private GoogleHttpClientService victim;
    @Mock
    private HttpTransport httpTransport;

    @Test
    void executeRequest() throws Exception {
        HttpRequestFactory httpRequestFactory = mock(HttpRequestFactory.class);
        HttpRequest httpRequest = mock(HttpRequest.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());

        when(httpTransport.createRequestFactory()).thenReturn(httpRequestFactory);
        when(httpRequestFactory.buildGetRequest(any(GenericUrl.class))).thenReturn(httpRequest);
        when(httpRequest.setHeaders(any(HttpHeaders.class))).thenReturn(httpRequest);
        when(httpRequest.execute()).thenReturn(httpResponse);
        when(httpResponse.getContent()).thenReturn(stream);
        when(httpResponse.getStatusCode()).thenReturn(200);

        ArgumentCaptor<GenericUrl> genericUrlArgumentCaptor = ArgumentCaptor.forClass(GenericUrl.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArgumentCaptor = ArgumentCaptor.forClass(HttpHeaders.class);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpRequest, times(1)).setHeaders(httpHeadersArgumentCaptor.capture());
        assertThat(httpHeadersArgumentCaptor.getValue()).containsEntry(HEADER_KEY_CLIENT_TYPE, GOOGLE_HTTP_CLIENT.getValue());

        verify(httpRequestFactory, times(1)).buildGetRequest(genericUrlArgumentCaptor.capture());
        assertThat(genericUrlArgumentCaptor.getValue()).hasToString(HTTP_URL);
        assertThat(genericUrlArgumentCaptor.getValue()).hasToString(HTTP_URL);
    }

}
