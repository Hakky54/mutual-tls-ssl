package nl.altindag.client.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.altindag.client.model.ClientResponse;

@Service
public class JerseyClientWrapper extends RequestService {

    private final Client client;

    @Autowired
    public JerseyClientWrapper(Client client) {
        this.client = client;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        Response response = client.target(url)
                                  .request(MediaType.TEXT_PLAIN_TYPE)
                                  .get();

        return new ClientResponse(response.readEntity(String.class), response.getStatus());
    }

}
