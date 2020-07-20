package nl.altindag.client.service;

import com.github.mizosoft.methanol.Methanol;
import com.github.mizosoft.methanol.MutableRequest;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;

import static nl.altindag.client.ClientType.METHANOL;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class MethanolService implements RequestService {

    private final Methanol httpClient;

    public MethanolService(@Qualifier("methanol") Methanol httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException, InterruptedException {
        MutableRequest request = MutableRequest.GET(url).header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return new ClientResponse(response.body(), response.statusCode());
    }

    @Override
    public ClientType getClientType() {
        return METHANOL;
    }

}
