package nl.altindag.client.service;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.function.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReactorNettyServiceShould {

    @InjectMocks
    private ReactorNettyService reactorNettyService;
    @Mock
    private HttpClient httpClient;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void executeRequest() throws Exception {
        HttpClient.ResponseReceiver responseReceiver = mock(HttpClient.ResponseReceiver.class);
        Tuple2<String, Integer> collection = mock(Tuple2.class);

        when(httpClient.headers(any(Consumer.class))).thenReturn(httpClient);
        when(httpClient.get()).thenReturn(responseReceiver);
        when(responseReceiver.uri(anyString())).thenReturn(responseReceiver);
        when(responseReceiver.responseSingle(any(BiFunction.class))).thenReturn(Mono.just(collection));
        when(collection.getT1()).thenReturn("hello");
        when(collection.getT2()).thenReturn(200);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Consumer<HttpHeaders>> headerCaptor = ArgumentCaptor.forClass(Consumer.class);

        ClientResponse clientResponse = reactorNettyService.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("hello");

        verify(responseReceiver, times(1)).uri(urlCaptor.capture());
        assertThat(urlCaptor.getValue()).isEqualTo(HTTP_URL);

        verify(httpClient, times(1)).headers(headerCaptor.capture());
        Consumer<HttpHeaders> httpHeadersConsumer = headerCaptor.getValue();
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        httpHeadersConsumer.accept(httpHeaders);
        assertThat(httpHeaders.get(HEADER_KEY_CLIENT_TYPE)).isEqualTo(ClientType.REACTOR_NETTY.getValue());
    }

}
