package nl.altindag.client.service;

import static nl.altindag.client.ClientType.FINAGLE;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.RequestBuilder;
import com.twitter.finagle.http.Response;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class FinagleHttpClientWrapper extends RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private com.twitter.finagle.Service<Request, Response> service;

    @Autowired
    public FinagleHttpClientWrapper(com.twitter.finagle.Service<Request, Response> finagleService) {
        this.service = finagleService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientResponse executeRequest(String url) throws Exception {
        Request request = new RequestBuilder()
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
