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
package nl.altindag.client.util;

import nl.altindag.client.ClientType;
import nl.altindag.log.LogCaptor;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.concurrent.TimeUnit;

/**
 * Use mock-server during unit test when mocking of certain classes or methods are not possible.
 * This option is to prevent exposing some static methods of libraries within your service class for enabling mocking
 */
public final class MockServerTestHelper {

    private static final ClientAndServer clientAndServer;

    static {
        clientAndServer = ClientAndServer.startClientAndServer(8080);
        Runtime.getRuntime().addShutdownHook(new Thread(clientAndServer::stop));

        LogCaptor.forName("org.mockserver.log.MockServerEventLog")
                .disableLogs();
    }

    public static void mockResponseForClient(ClientType clientType) {
        mockResponseForClient(clientType, TimeUnit.NANOSECONDS, 0);
    }

    public static void mockResponseForClient(ClientType clientType, TimeUnit responseDelayTimeUnit, long responseDelayAmount) {
        clientAndServer
                .when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/api/hello")
                                .withHeader("client-type", clientType.getValue()),
                        Times.unlimited(),
                        TimeToLive.unlimited())
                .respond(
                        HttpResponse.response()
                                .withBody("Hello")
                                .withStatusCode(200)
                                .withDelay(responseDelayTimeUnit, responseDelayAmount)
                );
    }
    
    public static void reset() {
        clientAndServer.reset();
    }

}
