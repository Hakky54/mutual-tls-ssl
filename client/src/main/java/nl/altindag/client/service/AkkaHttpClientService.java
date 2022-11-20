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

import static nl.altindag.client.ClientType.AKKA_HTTP_CLIENT;

import org.springframework.stereotype.Service;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;
import nl.altindag.client.ClientType;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;

@Service
public class AkkaHttpClientService implements RequestService {

    private final Http akkaHttpClient;
    private final ActorSystem actorSystem;

    public AkkaHttpClientService(Http akkaHttpClient, ActorSystem actorSystem) {
        this.akkaHttpClient = akkaHttpClient;
        this.actorSystem = actorSystem;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        return akkaHttpClient.singleRequest(HttpRequest.create(url).addHeader(HttpHeader.parse(Constants.HEADER_KEY_CLIENT_TYPE, getClientType().getValue())))
                .thenApply(httpResponse -> new ClientResponse(extractBody(httpResponse), httpResponse.status().intValue()))
                .toCompletableFuture()
                .join();
    }

    private String extractBody(HttpResponse httpResponse) {
        return httpResponse.entity()
                .getDataBytes()
                .fold(ByteString.emptyByteString(), ByteString::concat)
                .map(ByteString::utf8String)
                .runWith(Sink.head(), actorSystem)
                .toCompletableFuture()
                .join();
    }

    @Override
    public ClientType getClientType() {
        return AKKA_HTTP_CLIENT;
    }

}
