package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApacheCXFWebClientServiceShould {

    @InjectMocks
    private ApacheCXFWebClientService victim;
    @Mock
    private WebClient webClient;

    @Test
    void executeRequest() throws Exception {
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("Hello");

        when(webClient.to(HTTP_URL, false)).thenReturn(webClient);
        when(webClient.accept(MediaType.TEXT_PLAIN_TYPE)).thenReturn(webClient);
        when(webClient.header(HEADER_KEY_CLIENT_TYPE, ClientType.APACHE_CXF_WEB_CLIENT.getValue())).thenReturn(webClient);
        when(webClient.get()).thenReturn(response);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }
}
