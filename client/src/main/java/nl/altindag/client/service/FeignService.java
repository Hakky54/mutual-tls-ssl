package nl.altindag.client.service;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;

import static nl.altindag.client.Constants.HELLO_ENDPOINT;

public abstract class FeignService implements RequestService {

    private final Feign.Builder feign;

    protected FeignService(Feign.Builder feign) {
        this.feign = feign;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        String hello = feign.target(Server.class, Constants.getServerUrl())
                .getHello(getClientType().getValue());

        return new ClientResponse(hello, 200);
    }

    interface Server {

        @RequestLine("GET " + HELLO_ENDPOINT)
        @Headers(Constants.HEADER_KEY_CLIENT_TYPE + ": {client-type-value}")
        String getHello(@Param("client-type-value") String clientType);

    }

}
