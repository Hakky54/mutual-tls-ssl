package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static nl.altindag.client.ClientType.OK_HTTP;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class OkHttpClientService implements RequestService {

    private final OkHttpClient okHttpClient;

    public OkHttpClientService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        var request = new Request.Builder()
                .url(url)
                .header(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .build();

        var response = okHttpClient.newCall(request).execute();

        return new ClientResponse(Objects.requireNonNull(response.body()).string(), response.code());
    }

    @Override
    public ClientType getClientType() {
        return OK_HTTP;
    }
}
