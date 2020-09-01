package nl.altindag.client.service;

import nl.altindag.client.model.ClientResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nl.altindag.client.ClientType.JETTY_REACTIVE_HTTP_CLIENT;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JettyReactiveHttpClientServiceShould {

    @InjectMocks
    private JettyReactiveHttpClientService victim;
    @Mock
    private HttpClient httpClient;

    @Test
    void executeRequest() throws Exception {
        Request request = mock(Request.class);
        ContentResponse contentResponse = mock(ContentResponse.class);

        when(httpClient.newRequest(anyString())).thenReturn(request);
        when(request.method(any(HttpMethod.class))).thenReturn(request);
        when(request.header(anyString(), anyString())).thenReturn(request);
        when(request.send()).thenReturn(contentResponse);
        when(contentResponse.getContentAsString()).thenReturn("hello");
        when(contentResponse.getStatus()).thenReturn(200);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpMethod> httpMethodCaptor = ArgumentCaptor.forClass(HttpMethod.class);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("hello");

        verify(request, times(1)).header(headerKeyCaptor.capture(), headerValueCaptor.capture());
        assertThat(headerKeyCaptor.getValue()).isEqualTo(HEADER_KEY_CLIENT_TYPE);
        assertThat(headerValueCaptor.getValue()).isEqualTo(JETTY_REACTIVE_HTTP_CLIENT.getValue());

        verify(request, times(1)).method(httpMethodCaptor.capture());
        assertThat(httpMethodCaptor.getValue()).isEqualTo(HttpMethod.GET);

        verify(httpClient, times(1)).newRequest(urlCaptor.capture());
        assertThat(urlCaptor.getValue()).isEqualTo(HTTP_URL);

        verify(httpClient, times(1)).start();
        verify(httpClient, times(1)).stop();
    }

}
