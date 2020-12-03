package nl.altindag.client.service;

import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static nl.altindag.client.ClientType.SPRING_WEB_CLIENT_JETTY;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringWebClientJettyServiceShould {

    @InjectMocks
    private SpringWebClientJettyService victim;
    @Mock
    private WebClient webClient;

    @Test
    void getClientType() {
        assertThat(victim.getClientType()).isEqualTo(SPRING_WEB_CLIENT_JETTY);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void executeRequest() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        Mono<ResponseEntity<String>> responseEntityMono = Mono.just(responseEntity);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(HTTP_URL)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(HEADER_KEY_CLIENT_TYPE, SPRING_WEB_CLIENT_JETTY.getValue())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(responseEntityMono);
        when(responseEntity.getBody()).thenReturn("Hello");
        when(responseEntity.getStatusCodeValue()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

}
