package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApacheCXFJaxRsClientServiceShould {

    @InjectMocks
    private ApacheCXFJaxRsClientService victim;
    @Mock
    private Client client;

    @Test
    void executeRequest() {
        WebTarget webTarget = mock(WebTarget.class);
        Invocation.Builder requestBuilder = mock(Invocation.Builder.class);
        Response response = mock(Response.class);

        when(client.target(HTTP_URL)).thenReturn(webTarget);
        when(webTarget.request(MediaType.TEXT_PLAIN_TYPE)).thenReturn(requestBuilder);
        when(requestBuilder.header(HEADER_KEY_CLIENT_TYPE, ClientType.APACHE_CXF_JAX_RS.getValue())).thenReturn(requestBuilder);
        when(requestBuilder.get()).thenReturn(response);
        when(response.readEntity(String.class)).thenReturn("Hello");
        when(response.getStatus()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }
}
