package nl.altindag.client.service;

import nl.altindag.client.model.ClientResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static nl.altindag.client.ClientType.APACHE5_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.GET_METHOD;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Apache5HttpClientServiceShould {

    @InjectMocks
    private Apache5HttpClientService victim;
    @Mock
    private CloseableHttpClient httpClient;

    @Test
    void executeRequest() throws Exception {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        InputStream stream = new ByteArrayInputStream("Hello".getBytes());

        when(entity.getContent()).thenReturn(stream);
        when(response.getEntity()).thenReturn(entity);
        when(response.getCode()).thenReturn(200);

        when(httpClient.execute(any(ClassicHttpRequest.class))).thenReturn(response);

        ArgumentCaptor<ClassicHttpRequest> requestArgumentCaptor = ArgumentCaptor.forClass(ClassicHttpRequest.class);
        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        stream.close();

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpClient, times(1)).execute(requestArgumentCaptor.capture());
        assertThat(requestArgumentCaptor.getValue().getUri()).hasToString(HTTP_URL);
        assertThat(requestArgumentCaptor.getValue().getMethod()).isEqualTo(GET_METHOD);
        assertThat(requestArgumentCaptor.getValue().getHeaders()).hasSize(1);
        assertThat(requestArgumentCaptor.getValue().getFirstHeader(HEADER_KEY_CLIENT_TYPE).getValue()).isEqualTo(APACHE5_HTTP_CLIENT.getValue());
    }

}
