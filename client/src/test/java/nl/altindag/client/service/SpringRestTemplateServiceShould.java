package nl.altindag.client.service;

import nl.altindag.client.TestConstants;
import nl.altindag.client.model.ClientResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static nl.altindag.client.ClientType.SPRING_REST_TEMPLATE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringRestTemplateServiceShould {

    @InjectMocks
    private SpringRestTemplateService victim;
    @Mock
    private RestTemplate restTemplate;

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void executeRequest() {
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<HttpMethod> httpMethodArgumentCaptor = ArgumentCaptor.forClass(HttpMethod.class);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn("Hello");
        when(responseEntity.getStatusCodeValue()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(restTemplate, times(1)).exchange(anyString(), httpMethodArgumentCaptor.capture(), httpEntityArgumentCaptor.capture(), eq(String.class));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders()).containsExactly(Assertions.entry(TestConstants.HEADER_KEY_CLIENT_TYPE, Collections.singletonList(SPRING_REST_TEMPLATE.getValue())));
        assertThat(httpMethodArgumentCaptor.getValue()).isEqualTo(HttpMethod.GET);
    }

}
