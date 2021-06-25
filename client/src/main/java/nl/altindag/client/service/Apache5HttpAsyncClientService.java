package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static nl.altindag.client.ClientType.APACHE5_HTTP_ASYNC_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class Apache5HttpAsyncClientService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final CloseableHttpAsyncClient httpClient;

    public Apache5HttpAsyncClientService(CloseableHttpAsyncClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        var request = SimpleHttpRequests.get(url);
        request.addHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        Future<SimpleHttpResponse> responseFuture = httpClient.execute(request, null);

        SimpleHttpResponse response = responseFuture.get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);
        return new ClientResponse(response.getBodyText(), response.getCode());
    }

    @Override
    public ClientType getClientType() {
        return APACHE5_HTTP_ASYNC_CLIENT;
    }

}
