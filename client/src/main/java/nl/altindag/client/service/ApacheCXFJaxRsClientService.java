package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import static nl.altindag.client.ClientType.APACHE_CXF_JAX_RS;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

/**
 * CXF WebClient example {@link ApacheCXFWebClientService}
 * Another JAX-RS client {@link JerseyClientService}
 */
@Service
public class ApacheCXFJaxRsClientService implements RequestService {

    private final Client client;

    public ApacheCXFJaxRsClientService(@Qualifier("cxf") Client client) {
        this.client = client;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        var response = client.target(url)
                .request(MediaType.TEXT_PLAIN_TYPE)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .get();

        return new ClientResponse(response.readEntity(String.class), response.getStatus());
    }

    @Override
    public ClientType getClientType() {
        return APACHE_CXF_JAX_RS;
    }


}
