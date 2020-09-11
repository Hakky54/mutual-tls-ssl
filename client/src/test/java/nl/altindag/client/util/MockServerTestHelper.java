package nl.altindag.client.util;

import nl.altindag.client.ClientType;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 * Use mock-server during unit test when mocking of certain classes or methods are not possible.
 * This option is to prevent exposing some static methods of libraries within your service class for enabling mocking
 */
public final class MockServerTestHelper {

    private final ClientAndServer clientAndServer;

    public MockServerTestHelper(ClientType clientType) {
        clientAndServer = ClientAndServer.startClientAndServer(8080);
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
                );
    }

    public void stop() {
        clientAndServer.stop();
    }

}
