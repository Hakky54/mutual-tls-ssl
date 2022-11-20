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

import static nl.altindag.client.ClientType.GOOGLE_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class GoogleHttpClientService implements RequestService {

    private final HttpTransport httpTransport;

    public GoogleHttpClientService(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        HttpResponse response = httpTransport.createRequestFactory()
                .buildGetRequest(new GenericUrl(url))
                .setHeaders(new HttpHeaders().set(HEADER_KEY_CLIENT_TYPE, getClientType().getValue()))
                .execute();

        return new ClientResponse(IOUtils.toString(response.getContent(), StandardCharsets.UTF_8), response.getStatusCode());
    }

    @Override
    public ClientType getClientType() {
        return GOOGLE_HTTP_CLIENT;
    }

}
