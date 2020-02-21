package nl.altindag.client.service;

import dispatch.Http;
import nl.altindag.client.model.ClientResponse;
import org.asynchttpclient.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;

import static nl.altindag.client.ClientType.DISPATCH_REBOOT_HTTP_CLIENT;
import static nl.altindag.client.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DispatchRebootServiceShould {

    @InjectMocks
    private DispatchRebootService victim;
    @Mock
    private Http httpClient;

    @Test
    @SuppressWarnings("unchecked")
    public void executeRequest() throws Exception {
        Response response = mock(Response.class);
        DefaultAsyncHttpClient defaultAsyncHttpClient = mock(DefaultAsyncHttpClient.class);
        ListenableFuture<Response> listenableFuture = mock(ListenableFuture.class);

        when(httpClient.client()).thenReturn(defaultAsyncHttpClient);
        when(defaultAsyncHttpClient.executeRequest(any(RequestBuilder.class))).thenReturn(listenableFuture);
        when(listenableFuture.toCompletableFuture()).thenReturn(CompletableFuture.completedFuture(response));

        when(response.getStatusCode()).thenReturn(200);
        when(response.getResponseBody()).thenReturn("Hello");

        ArgumentCaptor<RequestBuilder> requestBuilderArgumentCaptor = ArgumentCaptor.forClass(RequestBuilder.class);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(defaultAsyncHttpClient, times(1)).executeRequest(requestBuilderArgumentCaptor.capture());
        Request request = requestBuilderArgumentCaptor.getValue().build();
        assertThat(request.getUrl()).isEqualTo(HTTP_URL);
        assertThat(request.getMethod()).isEqualTo(GET_METHOD);
        assertThat(request.getHeaders().get(HEADER_KEY_CLIENT_TYPE)).isEqualTo(DISPATCH_REBOOT_HTTP_CLIENT.getValue());
    }

}
