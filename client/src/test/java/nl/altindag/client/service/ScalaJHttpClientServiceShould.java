package nl.altindag.client.service;

import nl.altindag.client.model.ClientResponse;
import nl.altindag.sslcontext.SSLFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import scala.Function1;
import scalaj.http.HttpRequest;
import scalaj.http.HttpResponse;

import static nl.altindag.client.ClientType.SCALAJ_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ScalaJHttpClientServiceShould {

    @Spy
    @InjectMocks
    private ScalaJHttpClientService victim;
    @Mock
    private SSLFactory sslFactory;

    @Test
    public void executeRequest() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(victim.createRequest(HTTP_URL)).thenReturn(request);
        when(request.method("GET")).thenReturn(request);
        when(request.header(HEADER_KEY_CLIENT_TYPE, SCALAJ_HTTP_CLIENT.getValue())).thenReturn(request);
        when(request.option(any(Function1.class))).thenReturn(request);
        when(request.asString()).thenReturn(response);
        when(response.body()).thenReturn("Hello");
        when(response.code()).thenReturn(200);

        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");
    }

}
