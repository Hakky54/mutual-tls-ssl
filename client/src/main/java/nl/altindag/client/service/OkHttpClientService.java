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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static nl.altindag.client.ClientType.OK_HTTP;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class OkHttpClientService implements RequestService {

    private final OkHttpClient okHttpClient;

    public OkHttpClientService(@Qualifier("okHttpClient") OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        var request = new Request.Builder()
                .url(url)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .build();

        var response = okHttpClient.newCall(request).execute();

        return new ClientResponse(Objects.requireNonNull(response.body()).string(), response.code());
    }

    @Override
    public ClientType getClientType() {
        return OK_HTTP;
    }
}
