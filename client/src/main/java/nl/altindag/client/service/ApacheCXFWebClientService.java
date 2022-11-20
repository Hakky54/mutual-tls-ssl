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
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MediaType;

import static nl.altindag.client.ClientType.APACHE_CXF_WEB_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

/**
 * CXF JAX-RS example {@link ApacheCXFJaxRsClientService}
 */
@Service
public class ApacheCXFWebClientService implements RequestService {

    private final WebClient client;

    public ApacheCXFWebClientService(WebClient client) {
        this.client = client;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        var response = client.to(url, false)
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .get();

        return new ClientResponse(response.readEntity(String.class), response.getStatus());
    }

    @Override
    public ClientType getClientType() {
        return APACHE_CXF_WEB_CLIENT;
    }

}
