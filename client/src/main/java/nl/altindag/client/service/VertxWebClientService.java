package nl.altindag.client.service;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class VertxWebClientService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final WebClient client;

    public VertxWebClientService(WebClient client) {
        this.client = client;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        var uri = URI.create(url);

        HttpResponse<Buffer> response = client.get(uri.getPort(), uri.getHost(), uri.getPath())
                .putHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);

        return new ClientResponse(response.bodyAsString(), response.statusCode());
    }

    @Override
    public ClientType getClientType() {
        return ClientType.VERTX;
    }

}
