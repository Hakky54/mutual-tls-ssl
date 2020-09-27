package nl.altindag.client.service;

import kong.unirest.Unirest;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.util.MockServerTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nl.altindag.client.ClientType.UNIREST;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;

class UnirestServiceShould {

    private UnirestService victim;

    @BeforeEach
    void setUp() {
        victim = new UnirestService();
    }

    @Test
    void executeRequest() {
        MockServerTestHelper.mockResponseForClient(UNIREST);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

    @AfterEach
    void tearDown() {
        Unirest.shutDown();
    }

}
