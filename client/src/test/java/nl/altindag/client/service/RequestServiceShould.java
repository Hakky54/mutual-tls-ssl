package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.TestConstants;
import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestServiceShould {

    @Test
    void throwClientExceptionWhenCheckedExceptionIsThrown() {
        RequestService requestService = new RequestService() {
            @Override
            public ClientResponse executeRequest(String url) throws Exception {
                throw new IOException("KABOOM");
            }

            @Override
            public ClientType getClientType() {
                return ClientType.NONE;
            }
        };

        assertThatThrownBy(() -> requestService.execute(TestConstants.HTTP_URL))
                .hasMessageContaining("KABOOM");
    }
}
