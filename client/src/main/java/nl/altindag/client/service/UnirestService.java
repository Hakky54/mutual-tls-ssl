package nl.altindag.client.service;

import static nl.altindag.client.ClientType.UNIREST;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class UnirestService implements RequestService {

    @Override
    public ClientResponse executeRequest(String url) {
        HttpResponse<String> response = Unirest.get(url)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .asString();

        return new ClientResponse(response.getBody(), response.getStatus());
    }

    @Override
    public ClientType getClientType() {
        return UNIREST;
    }
}
