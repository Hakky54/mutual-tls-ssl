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
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static nl.altindag.client.ClientType.ASYNC_HTTP_CLIENT;
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
class AsyncHttpClientServiceShould {

    @InjectMocks
    private AsyncHttpClientService victim;
    @Mock
    private AsyncHttpClient httpClient;

    @Test
    @SuppressWarnings("unchecked")
    void executeRequest() throws Exception {
        Response response = mock(Response.class);
        ListenableFuture<Response> listenableFuture = mock(ListenableFuture.class);

        when(httpClient.executeRequest(any(RequestBuilder.class))).thenReturn(listenableFuture);
        when(listenableFuture.toCompletableFuture()).thenReturn(CompletableFuture.completedFuture(response));

        when(response.getStatusCode()).thenReturn(200);
        when(response.getResponseBody()).thenReturn("Hello");

        ArgumentCaptor<RequestBuilder> requestBuilderArgumentCaptor = ArgumentCaptor.forClass(RequestBuilder.class);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpClient, times(1)).executeRequest(requestBuilderArgumentCaptor.capture());
        Request request = requestBuilderArgumentCaptor.getValue().build();
        assertThat(request.getUrl()).isEqualTo(HTTP_URL);
        assertThat(request.getMethod()).isEqualTo(GET_METHOD);
        assertThat(request.getHeaders().get(HEADER_KEY_CLIENT_TYPE)).isEqualTo(ASYNC_HTTP_CLIENT.getValue());
    }

}
