package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;
import org.springframework.stereotype.Service;

import static nl.altindag.client.ClientType.JETTY_REACTIVE_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class JettyReactiveHttpClientService implements RequestService {

    private final HttpClient httpClient;

    public JettyReactiveHttpClientService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        httpClient.start();

        var contentResponse = httpClient.newRequest(url)
                .method(HttpMethod.GET)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .send();

        httpClient.stop();

        return new ClientResponse(contentResponse.getContentAsString(), contentResponse.getStatus());
    }

    @Override
    public ClientType getClientType() {
        return JETTY_REACTIVE_HTTP_CLIENT;
    }
}
