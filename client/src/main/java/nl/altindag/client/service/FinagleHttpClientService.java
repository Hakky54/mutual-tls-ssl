package nl.altindag.client.service;

import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.RequestBuilder;
import com.twitter.finagle.http.Response;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static nl.altindag.client.ClientType.FINAGLE;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class FinagleHttpClientService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final com.twitter.finagle.Service<Request, Response> service;

    public FinagleHttpClientService(com.twitter.finagle.Service<Request, Response> finagleService) {
        this.service = finagleService;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        var request = new RequestBuilder<>()
                .addHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .url(url)
                .buildGet(null);

        return service.apply(request)
                .map(response -> new ClientResponse(response.contentString(), response.statusCode()))
                .toJavaFuture()
                .get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public ClientType getClientType() {
        return FINAGLE;
    }
}
