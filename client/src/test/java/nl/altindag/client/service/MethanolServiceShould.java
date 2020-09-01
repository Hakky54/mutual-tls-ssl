package nl.altindag.client.service;

import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MutableRequest;
import nl.altindag.client.model.ClientResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpResponse;
import java.util.Collections;

import static nl.altindag.client.ClientType.METHANOL;
import static nl.altindag.client.TestConstants.GET_METHOD;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MethanolServiceShould {

    @InjectMocks
    private MethanolService victim;
    @Mock
    private Methanol httpClient;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void executeRequest() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);
        when(httpClient.send(any(MutableRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("Hello");

        ArgumentCaptor<MutableRequest> httpRequestArgumentCaptor = ArgumentCaptor.forClass(MutableRequest.class);
        ArgumentCaptor<HttpResponse.BodyHandler<String>> bodyHandlerArgumentCaptor = ArgumentCaptor.forClass(HttpResponse.BodyHandler.class);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpClient, times(1)).send(httpRequestArgumentCaptor.capture(), bodyHandlerArgumentCaptor.capture());
        assertThat(httpRequestArgumentCaptor.getValue().uri()).hasToString(HTTP_URL);
        assertThat(httpRequestArgumentCaptor.getValue().method()).isEqualTo(GET_METHOD);
        assertThat(httpRequestArgumentCaptor.getValue().headers().map()).containsExactly(Assertions.entry(HEADER_KEY_CLIENT_TYPE, Collections.singletonList(METHANOL.getValue())));
        assertThat(bodyHandlerArgumentCaptor.getValue()).isEqualTo(HttpResponse.BodyHandlers.ofString());
    }

}
