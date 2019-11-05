package nl.altindag.client;

import org.springframework.stereotype.Component;

import nl.altindag.client.model.ClientResponse;

@Component
public class TestScenario {

    private ClientResponse clientResponse;

    public ClientResponse getClientResponse() {
        return clientResponse;
    }

    public void setClientResponse(ClientResponse clientResponse) {
        this.clientResponse = clientResponse;
    }

}
