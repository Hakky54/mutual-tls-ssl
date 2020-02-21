package nl.altindag.client.service;

import dispatch.Http;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static nl.altindag.client.ClientType.DISPATCH_REBOOT_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class DispatchRebootService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final Http httpClient;

    @Autowired
    public DispatchRebootService(Http httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        RequestBuilder requestBuilder = new RequestBuilder()
                .setUrl(url)
                .setHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        Response response = httpClient.client().executeRequest(requestBuilder)
                .toCompletableFuture()
                .get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);

        return new ClientResponse(response.getResponseBody(), response.getStatusCode());
    }

    @Override
    public ClientType getClientType() {
        return DISPATCH_REBOOT_HTTP_CLIENT;
    }

}
