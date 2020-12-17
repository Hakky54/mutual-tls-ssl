package nl.altindag.client.service;

import nl.altindag.client.model.ClientResponse;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static nl.altindag.client.ClientType.APACHE5_HTTP_ASYNC_CLIENT;
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
class Apache5HttpAsyncClientServiceShould {

    @InjectMocks
    private Apache5HttpAsyncClientService victim;
    @Mock
    private CloseableHttpAsyncClient httpClient;

    @Test
    void executeRequest() throws Exception {
        SimpleHttpResponse response = mock(SimpleHttpResponse.class);

        when(response.getBodyText()).thenReturn("Hello");
        when(response.getCode()).thenReturn(200);

        when(httpClient.execute(any(SimpleHttpRequest.class), any()))
                .thenReturn(CompletableFuture.completedFuture(response));

        ArgumentCaptor<SimpleHttpRequest> requestArgumentCaptor = ArgumentCaptor.forClass(SimpleHttpRequest.class);
        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpClient, times(1)).execute(requestArgumentCaptor.capture(), any());
        assertThat(requestArgumentCaptor.getValue().getUri()).hasToString(HTTP_URL);
        assertThat(requestArgumentCaptor.getValue().getMethod()).isEqualTo(GET_METHOD);
        assertThat(requestArgumentCaptor.getValue().getHeaders()).hasSize(1);
        assertThat(requestArgumentCaptor.getValue().getFirstHeader(HEADER_KEY_CLIENT_TYPE).getValue()).isEqualTo(APACHE5_HTTP_ASYNC_CLIENT.getValue());
    }

}
