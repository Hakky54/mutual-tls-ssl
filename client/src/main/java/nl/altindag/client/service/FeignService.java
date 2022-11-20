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

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;

import static nl.altindag.client.Constants.HELLO_ENDPOINT;

public abstract class FeignService implements RequestService {

    private final Feign.Builder feign;

    protected FeignService(Feign.Builder feign) {
        this.feign = feign;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        String hello = feign.target(Server.class, Constants.getServerUrl())
                .getHello(getClientType().getValue());

        return new ClientResponse(hello, 200);
    }

    interface Server {

        @RequestLine("GET " + HELLO_ENDPOINT)
        @Headers(Constants.HEADER_KEY_CLIENT_TYPE + ": {client-type-value}")
        String getHello(@Param("client-type-value") String clientType);

    }

}
