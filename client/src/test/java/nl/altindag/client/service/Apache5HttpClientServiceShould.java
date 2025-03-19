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
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nl.altindag.client.ClientType.APACHE5_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.GET_METHOD;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Apache5HttpClientServiceShould {

    @InjectMocks
    private Apache5HttpClientService victim;
    @Mock
    private CloseableHttpClient httpClient;

    @Test
    void executeRequest() throws Exception {
        ClientResponse mockClientResponse = new ClientResponse("Hello", 200);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class))).thenReturn(mockClientResponse);

        ArgumentCaptor<ClassicHttpRequest> requestArgumentCaptor = ArgumentCaptor.forClass(ClassicHttpRequest.class);
        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpClient, times(1)).execute(requestArgumentCaptor.capture(), any(HttpClientResponseHandler.class));
        assertThat(requestArgumentCaptor.getValue().getUri()).hasToString(HTTP_URL);
        assertThat(requestArgumentCaptor.getValue().getMethod()).isEqualTo(GET_METHOD);
        assertThat(requestArgumentCaptor.getValue().getHeaders()).hasSize(1);
        assertThat(requestArgumentCaptor.getValue().getFirstHeader(HEADER_KEY_CLIENT_TYPE).getValue()).isEqualTo(APACHE5_HTTP_CLIENT.getValue());
    }

}
