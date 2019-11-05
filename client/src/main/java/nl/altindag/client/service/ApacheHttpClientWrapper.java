package nl.altindag.client.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.altindag.client.model.ClientResponse;

@Service
public class ApacheHttpClientWrapper extends RequestService {

    private final HttpClient httpClient;

    @Autowired
    public ApacheHttpClientWrapper(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);

        String responseBody = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        return new ClientResponse(responseBody, statusCode);
    }

}
