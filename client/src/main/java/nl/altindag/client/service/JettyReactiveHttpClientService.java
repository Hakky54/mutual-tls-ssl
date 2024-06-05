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
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.springframework.stereotype.Service;

import static nl.altindag.client.ClientType.JETTY_REACTIVE_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class JettyReactiveHttpClientService implements RequestService {

    private final HttpClient httpClient;

    public JettyReactiveHttpClientService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        httpClient.start();

        var contentResponse = httpClient.newRequest(url)
                .method(HttpMethod.GET)
                .headers(header -> header.add(HEADER_KEY_CLIENT_TYPE, getClientType().getValue()))
                .send();

        httpClient.stop();

        return new ClientResponse(contentResponse.getContentAsString(), contentResponse.getStatus());
    }

    @Override
    public ClientType getClientType() {
        return JETTY_REACTIVE_HTTP_CLIENT;
    }
}
