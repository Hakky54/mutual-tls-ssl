package nl.altindag.client.service;

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
                                                                        .get(com.sun.jersey.api.client.ClientResponse.class);

        return new ClientResponse(clientResponse.getEntity(String.class), clientResponse.getStatus());
    }
}
