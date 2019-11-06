package nl.altindag.client.service;

import org.springframework.stereotype.Service;

import nl.altindag.client.model.ClientResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class OkHttpClientWrapper extends RequestService {

    private final OkHttpClient okHttpClient;

    public OkHttpClientWrapper(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        Request request = new Request.Builder()
                                     .url(url)
                                     .build();

        Response response = okHttpClient.newCall(request).execute();

        return new ClientResponse(response.body().string(), response.code());
    }

}
