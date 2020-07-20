package nl.altindag.client.service;

import static nl.altindag.client.ClientType.JDK_HTTP_CLIENT;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class JdkHttpClientService implements RequestService {

    private final HttpClient httpClient;

    public JdkHttpClientService(@Qualifier("jdkHttpClient") HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return new ClientResponse(response.body(), response.statusCode());
    }

    @Override
    public ClientType getClientType() {
        return JDK_HTTP_CLIENT;
    }

}
