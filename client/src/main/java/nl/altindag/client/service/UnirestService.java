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

import static nl.altindag.client.ClientType.UNIREST;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class UnirestService implements RequestService {

    @Override
    public ClientResponse executeRequest(String url) {
        HttpResponse<String> response = Unirest.get(url)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .asString();

        return new ClientResponse(response.getBody(), response.getStatus());
    }

    @Override
    public ClientType getClientType() {
        return UNIREST;
    }
}
