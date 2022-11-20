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

import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import org.springframework.web.reactive.function.client.WebClient;

import nl.altindag.client.model.ClientResponse;

public abstract class SpringWebClientService implements RequestService {

    private final WebClient webClient;

    protected SpringWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        return webClient.get()
                        .uri(url)
                        .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                        .exchangeToMono(clientResponse -> clientResponse.toEntity(String.class))
                        .map(responseEntity -> new ClientResponse(responseEntity.getBody(), responseEntity.getStatusCodeValue()))
                        .block();
    }

}
