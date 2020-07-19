package nl.altindag.client.service;

import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import nl.altindag.client.ClientType;
import nl.altindag.client.Constants;
import nl.altindag.client.model.ClientResponse;
import org.springframework.stereotype.Service;

import static nl.altindag.client.ClientType.FEIGN;
import static nl.altindag.client.Constants.HELLO_ENDPOINT;

@Service
public class FeignService implements RequestService {

    private final Feign.Builder feign;

    public FeignService(Feign.Builder feign) {
        this.feign = feign;
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        String hello = feign.target(Server.class, Constants.SERVER_URL)
                .getHello();

        return new ClientResponse(hello, 200);
    }

    @Override
    public ClientType getClientType() {
        return FEIGN;
    }

    interface Server {

        @RequestLine("GET " + HELLO_ENDPOINT)
        @Headers(Constants.HEADER_KEY_CLIENT_TYPE + ": feign")
        String getHello();

    }

}
