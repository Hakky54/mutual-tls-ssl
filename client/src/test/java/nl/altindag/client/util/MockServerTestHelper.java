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
