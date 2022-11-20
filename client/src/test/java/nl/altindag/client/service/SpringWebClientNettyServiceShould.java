/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import static nl.altindag.client.ClientType.SPRING_WEB_CLIENT_NETTY;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringWebClientNettyServiceShould {

    @InjectMocks
    private SpringWebClientNettyService victim;
    @Mock
    private WebClient webClient;

    @Test
    void getClientType() {
        assertThat(victim.getClientType()).isEqualTo(SPRING_WEB_CLIENT_NETTY);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void executeRequest() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        Mono<ResponseEntity<String>> responseEntityMono = Mono.just(responseEntity);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(HTTP_URL)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.header(HEADER_KEY_CLIENT_TYPE, SPRING_WEB_CLIENT_NETTY.getValue())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(responseEntityMono);
        when(responseEntity.getBody()).thenReturn("Hello");
        when(responseEntity.getStatusCodeValue()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

}
