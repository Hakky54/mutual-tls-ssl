package nl.altindag.client.service;

import io.vertx.core.impl.future.SucceededFuture;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "unchecked"})
@ExtendWith(MockitoExtension.class)
class VertxWebClientServiceShould {

    @Test
    void executeRequest() throws Exception {
        WebClient webClient = mock(WebClient.class);
        HttpRequest httpRequest = mock(HttpRequest.class);
        HttpResponse httpResponse = mock(HttpResponse.class);

        when(webClient.get(anyInt(), anyString(), anyString())).thenReturn(httpRequest);
        when(httpRequest.putHeader(HEADER_KEY_CLIENT_TYPE, "vertx webclient")).thenReturn(httpRequest);
        when(httpRequest.send()).thenReturn(new SucceededFuture(httpResponse));
        when(httpResponse.bodyAsString()).thenReturn("Hello");
        when(httpResponse.statusCode()).thenReturn(200);

        RequestService victim = new VertxWebClientService(webClient);
        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

}
