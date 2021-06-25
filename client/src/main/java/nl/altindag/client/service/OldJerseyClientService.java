package nl.altindag.client.service;

import static nl.altindag.client.ClientType.OLD_JERSEY_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class OldJerseyClientService implements RequestService {

    private final Client client;

    public OldJerseyClientService(Client client) {
        this.client = client;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        var clientResponse = client.resource(url)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .get(com.sun.jersey.api.client.ClientResponse.class);

        return new ClientResponse(clientResponse.getEntity(String.class), clientResponse.getStatus());
    }

    @Override
    public ClientType getClientType() {
        return OLD_JERSEY_CLIENT;
    }
}
