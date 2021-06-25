package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static nl.altindag.client.ClientType.APACHE_HTTP_ASYNC_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class ApacheHttpAsyncClientService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final CloseableHttpAsyncClient httpClient;

    public ApacheHttpAsyncClientService(CloseableHttpAsyncClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        var request = new HttpGet(url);
        request.addHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());

        Future<HttpResponse> responseFuture = httpClient.execute(request, null);
        HttpResponse response = responseFuture.get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);

        var responseBody = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        return new ClientResponse(responseBody, statusCode);
    }

    @Override
    public ClientType getClientType() {
        return APACHE_HTTP_ASYNC_CLIENT;
    }

}
