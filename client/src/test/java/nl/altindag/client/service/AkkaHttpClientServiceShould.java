package nl.altindag.client.service;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import nl.altindag.client.TestConstants;
import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static nl.altindag.client.ClientType.AKKA_HTTP_CLIENT;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AkkaHttpClientServiceShould {

    private AkkaHttpClientService victim;
    private Http akkaHttpClient;

    @BeforeEach
    void setUp() {
        akkaHttpClient = mock(Http.class);
        victim = new AkkaHttpClientService(akkaHttpClient, ActorSystem.create());
    }

    @Test
    void executeRequest() {
        HttpResponse httpResponse = HttpResponse.create()
                .withEntity(ContentTypes.TEXT_PLAIN_UTF8, "Hello")
                .withStatus(StatusCodes.OK);

        when(akkaHttpClient.singleRequest(any(HttpRequest.class))).thenReturn(CompletableFuture.completedFuture(httpResponse));

        ArgumentCaptor<HttpRequest> httpRequestArgumentCaptor = ArgumentCaptor.forClass(HttpRequest.class);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(akkaHttpClient, times(1)).singleRequest(httpRequestArgumentCaptor.capture());
        assertThat(httpRequestArgumentCaptor.getValue().method().value()).isEqualTo("GET");
        assertThat(httpRequestArgumentCaptor.getValue().getHeaders()).containsExactly(HttpHeader.parse(TestConstants.HEADER_KEY_CLIENT_TYPE, AKKA_HTTP_CLIENT.getValue()));
        assertThat(httpRequestArgumentCaptor.getValue().getUri()).hasToString(HTTP_URL);
    }

}
