package nl.altindag.client.service;

import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.Constants.OLD_JERSEY_CLIENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;

import nl.altindag.client.model.ClientResponse;

@Service
public class OldJerseyClientWrapper extends RequestService {

    private final Client client;

    @Autowired
    public OldJerseyClientWrapper(Client client) {
        this.client = client;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        com.sun.jersey.api.client.ClientResponse clientResponse = client.resource(url)
                                                                        .header(HEADER_KEY_CLIENT_TYPE, OLD_JERSEY_CLIENT)
                                                                        .get(com.sun.jersey.api.client.ClientResponse.class);

        return new ClientResponse(clientResponse.getEntity(String.class), clientResponse.getStatus());
    }
}
