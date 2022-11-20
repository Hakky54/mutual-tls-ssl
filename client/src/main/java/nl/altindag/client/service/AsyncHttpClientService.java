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

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.RequestBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static nl.altindag.client.ClientType.ASYNC_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class AsyncHttpClientService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final AsyncHttpClient httpClient;

    public AsyncHttpClientService(AsyncHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        var requestBuilder = new RequestBuilder()
                .setUrl(url)
                .setHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        var response = httpClient.executeRequest(requestBuilder)
                .toCompletableFuture()
                .get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);

        return new ClientResponse(response.getResponseBody(), response.getStatusCode());
    }

    @Override
    public ClientType getClientType() {
        return ASYNC_HTTP_CLIENT;
    }

}
