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

import static nl.altindag.client.ClientType.SPRING_WEB_CLIENT_NETTY;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import nl.altindag.client.ClientType;

@Service
public class SpringWebClientNettyService extends SpringWebClientService {

    public SpringWebClientNettyService(@Qualifier("webClientWithNetty") WebClient webClientWithNetty) {
        super(webClientWithNetty);
    }

    @Override
    public ClientType getClientType() {
        return SPRING_WEB_CLIENT_NETTY;
    }

}
