package nl.altindag.client.util;

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
    private final MockServerClient mockServerClient;

    public MockServerTestHelper(String clientType) {
        clientAndServer = ClientAndServer.startClientAndServer(8080);
        mockServerClient = new MockServerClient("127.0.0.1", 8080);
        mockServerClient
                .when(
                        HttpRequest.request()
                                .withMethod("GET")
                                .withPath("/api/hello")
                                .withHeader("client-type", clientType),
                        Times.unlimited(),
                        TimeToLive.unlimited())
                .respond(
                        HttpResponse.response()
                                .withBody("Hello")
                                .withStatusCode(200)
                );
    }

    public void stop() {
        mockServerClient.stop();
        mockServerClient.close();
        clientAndServer.stop();
        clientAndServer.close();
    }

}
