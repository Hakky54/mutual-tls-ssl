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
import nl.altindag.client.ClientType;
import nl.altindag.client.TestConstants;
import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeignOkHttpClientServiceShould {

    @InjectMocks
    private FeignOkHttpClientService victim;
    @Mock
    private Feign.Builder feignBuilder;

    @Test
    void executeRequest() throws Exception {
        FeignService.Server server = mock(FeignService.Server.class);

        Mockito.lenient().doReturn(server).when(feignBuilder).target(FeignService.Server.class, TestConstants.HTTP_SERVER_URL);
        Mockito.lenient().doReturn(server).when(feignBuilder).target(FeignService.Server.class, TestConstants.HTTPS_SERVER_URL);
        when(server.getHello(ClientType.FEIGN_OK_HTTP_CLIENT.getValue())).thenReturn("Hello");

        ClientResponse clientResponse = victim.executeRequest(null);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

    @Test
    void getClientType() {
        assertThat(victim.getClientType()).isEqualTo(ClientType.FEIGN_OK_HTTP_CLIENT);
    }

}
