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

import static nl.altindag.client.ClientType.OLD_JERSEY_CLIENT;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import nl.altindag.client.model.ClientResponse;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OldJerseyClientServiceShould {

    @InjectMocks
    private OldJerseyClientService victim;
    @Mock
    private Client client;

    @Test
    void executeRequest() {
        WebResource webResource = mock(WebResource.class);
        WebResource.Builder builder = mock(WebResource.Builder.class);
        com.sun.jersey.api.client.ClientResponse response = mock(com.sun.jersey.api.client.ClientResponse.class);

        when(client.resource(HTTP_URL)).thenReturn(webResource);
        when(webResource.header(HEADER_KEY_CLIENT_TYPE, OLD_JERSEY_CLIENT.getValue())).thenReturn(builder);
        when(builder.get(com.sun.jersey.api.client.ClientResponse.class)).thenReturn(response);
        when(response.getEntity(String.class)).thenReturn("Hello");
        when(response.getStatus()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

}
