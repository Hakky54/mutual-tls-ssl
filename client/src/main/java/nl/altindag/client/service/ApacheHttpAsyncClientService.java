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
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static nl.altindag.client.ClientType.APACHE_HTTP_ASYNC_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class ApacheHttpAsyncClientService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final CloseableHttpAsyncClient httpClient;

    public ApacheHttpAsyncClientService(CloseableHttpAsyncClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        var request = new HttpGet(url);
        request.addHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        Future<HttpResponse> responseFuture = httpClient.execute(request, null);
        HttpResponse response = responseFuture.get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);

        var responseBody = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        return new ClientResponse(responseBody, statusCode);
    }

    @Override
    public ClientType getClientType() {
        return APACHE_HTTP_ASYNC_CLIENT;
    }

}
