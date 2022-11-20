/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
