package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static nl.altindag.client.ClientType.REACTOR_NETTY;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class ReactorNettyService implements RequestService {

    private final HttpClient httpClient;

    public ReactorNettyService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        return httpClient.headers(headers -> headers.set(HEADER_KEY_CLIENT_TYPE, getClientType().getValue()))
                .get()
                .uri(url)
                .responseSingle((response, body) -> Mono.zip(body.asString(), Mono.just(response.status().code())))
                .map(tuple -> new ClientResponse(tuple.getT1(), tuple.getT2()))
                .block();
    }

    @Override
    public ClientType getClientType() {
        return REACTOR_NETTY;
    }

}
