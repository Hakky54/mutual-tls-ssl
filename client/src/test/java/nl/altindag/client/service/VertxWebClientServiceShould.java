package nl.altindag.client.service;

import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.util.MockServerTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static nl.altindag.client.ClientType.VERTX;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class VertxWebClientServiceShould {

    @Test
    void executeRequest() throws Exception {
        MockServerTestHelper.mockResponseForClient(VERTX);

        RequestService victim = new VertxWebClientService(null);
        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

}
