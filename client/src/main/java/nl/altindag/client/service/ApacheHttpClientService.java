package nl.altindag.client.service;

import static nl.altindag.client.ClientType.APACHE_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class ApacheHttpClientService implements RequestService {

    private final CloseableHttpClient httpClient;

    public ApacheHttpClientService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        var request = new HttpGet(url);
        request.addHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());
        HttpResponse response = httpClient.execute(request);

        var responseBody = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        return new ClientResponse(responseBody, statusCode);
    }

    @Override
    public ClientType getClientType() {
        return APACHE_HTTP_CLIENT;
    }

}
