package nl.altindag.client.util;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Use mock-server during unit test when mocking of certain classes or methods are not possible.
 * This option is to prevent exposing some static methods of libraries within your service class for enabling mocking
 */
public final class MockServerTestHelper {

    private ClientAndServer clientAndServer;

    public MockServerTestHelper(String clientType) {
        clientAndServer = startClientAndServer(8080);
        new MockServerClient("127.0.0.1", 8080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/hello")
                                .withHeader("client-type", clientType),
                        exactly(1))
                .respond(
                        response()
                                .withBody("Hello")
                                .withStatusCode(200)
                );
    }

    public void stop() {
        clientAndServer.stop();
    }

}
