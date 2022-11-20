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

import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MutableRequest;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;

import static nl.altindag.client.ClientType.METHANOL;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class MethanolService implements RequestService {

    private final Methanol httpClient;

    public MethanolService(@Qualifier("methanol") Methanol httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException, InterruptedException {
        MutableRequest request = MutableRequest.GET(url).header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return new ClientResponse(response.body(), response.statusCode());
    }

    @Override
    public ClientType getClientType() {
        return METHANOL;
    }

}
