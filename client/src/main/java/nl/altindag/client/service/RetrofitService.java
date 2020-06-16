package nl.altindag.client.service;

import nl.altindag.client.ClientType;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.io.IOException;

import static nl.altindag.client.ClientType.RETROFIT;
import static nl.altindag.client.Constants.HELLO_ENDPOINT;

@Service
public class RetrofitService implements RequestService {

    private final Retrofit retrofit;

    public RetrofitService(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public ClientResponse executeRequest(String url) throws IOException {
        Response<String> response = retrofit.create(Server.class)
                .getHello()
                .execute();

        return new ClientResponse(response.body(), response.code());
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
