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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static nl.altindag.client.ClientType.REACTOR_NETTY;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class ReactorNettyService implements RequestService {

    private final HttpClient httpClient;

    public ReactorNettyService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        return httpClient.headers(headers -> headers.set(HEADER_KEY_CLIENT_TYPE, getClientType().getValue()))
                .get()
                .uri(url)
                .responseSingle((response, body) -> Mono.zip(body.asString(), Mono.just(response.status().code())))
                .map(tuple -> new ClientResponse(tuple.getT1(), tuple.getT2()))
                .block();
    }

    @Override
    public ClientType getClientType() {
        return REACTOR_NETTY;
    }

}
