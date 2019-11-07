package nl.altindag.client.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.web.reactive.function.client.WebClient;

import nl.altindag.client.ClientException;
import nl.altindag.client.model.ClientResponse;

public class SpringWebClientWrapper extends RequestService {

    private static final int TIMOUT_AMOUNT_OF_SECONDS = 1;

    private final WebClient webClient;

    public SpringWebClientWrapper(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        Optional<ClientResponse> response = webClient.get()
                                                     .uri(url)
                                                     .exchange()
                                                     .flatMap(clientResponse -> clientResponse.toEntity(String.class))
                                                     .map(responseEntity -> new ClientResponse(responseEntity.getBody(), responseEntity.getStatusCodeValue()))
                                                     .blockOptional(Duration.ofSeconds(TIMOUT_AMOUNT_OF_SECONDS));

        if (response.isPresent()) {
            return response.get();
        } else {
            throw new ClientException(String.format("Could not get a response from the server within %d seconds", TIMOUT_AMOUNT_OF_SECONDS));
        }
    }

}
