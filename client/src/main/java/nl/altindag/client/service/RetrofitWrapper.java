package nl.altindag.client.service;

import static nl.altindag.client.ClientType.RETROFIT;
import static nl.altindag.client.Constants.HELLO_ENDPOINT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;

@Service
public class RetrofitWrapper implements RequestService {

    private final Retrofit retrofit;

    @Autowired
    public RetrofitWrapper(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        try {
            Response<String> response = retrofit.create(Server.class)
                                                .getHello()
                                                .execute();

            return new ClientResponse(response.body(), response.code());
        } catch (IOException e) {
            throw new ClientException(String.format("could not execute the request, received the following message: %s", e.getMessage()));
        }
    }

    @Override
    public ClientType getClientType() {
        return RETROFIT;
    }

    interface Server {

        @GET(HELLO_ENDPOINT)
        @Headers(Constants.HEADER_KEY_CLIENT_TYPE + ": retrofit")
        Call<String> getHello();

    }

}
