package nl.altindag.client.service;

import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.twitter.finagle.Service;
import com.twitter.finagle.http.Method;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.util.Future;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import scala.Tuple2;

@ExtendWith(MockitoExtension.class)
class FinagleHttpClientServiceShould {

    @InjectMocks
    private FinagleHttpClientService victim;
    @Mock
    private Service<Request, Response> finagleService;

    @Test
    void executeRequest() throws Exception {
        Response response = mock(Response.class);

        when(finagleService.apply(Mockito.any(Request.class))).thenReturn(Future.value(response));
        when(response.statusCode()).thenReturn(200);
        when(response.contentString()).thenReturn("Hello");

        ArgumentCaptor<Request> requestArgumentCaptor = ArgumentCaptor.forClass(Request.class);
        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(finagleService, times(1)).apply(requestArgumentCaptor.capture());
        assertThat(requestArgumentCaptor.getValue().method()).isEqualTo(Method.Get());
        assertThat(requestArgumentCaptor.getValue().uri()).isEqualTo(URI.create(HTTP_URL).getPath());

        URI uri = URI.create(HTTP_URL);
        assertThat(requestArgumentCaptor.getValue().headerMap().toSet().contains(Tuple2.apply("Host", uri.getHost() + ":" + uri.getPort()))).isTrue();
        assertThat(requestArgumentCaptor.getValue().headerMap().toSet().contains(Tuple2.apply(HEADER_KEY_CLIENT_TYPE, ClientType.FINAGLE.getValue()))).isTrue();
    }

}
