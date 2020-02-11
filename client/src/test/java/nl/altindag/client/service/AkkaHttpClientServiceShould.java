package nl.altindag.client.service;

import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import nl.altindag.client.TestConstants;
import nl.altindag.client.model.ClientResponse;

@RunWith(MockitoJUnitRunner.class)
public class AkkaHttpClientServiceShould {

    private AkkaHttpClientService victim;
    private Http akkaHttpClient;

    @Before
    public void setUp() {
        akkaHttpClient = mock(Http.class);
        victim = new AkkaHttpClientService(akkaHttpClient, ActorSystem.create());
    }

    @Test
    public void executeRequest() {
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
        assertThat(httpRequestArgumentCaptor.getValue().getHeaders()).containsExactly(HttpHeader.parse(TestConstants.HEADER_KEY_CLIENT_TYPE, "akka http client"));
        assertThat(httpRequestArgumentCaptor.getValue().getUri().toString()).isEqualTo(HTTP_URL);
    }

}
