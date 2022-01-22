package nl.altindag.client.service;

import feign.Feign;
import nl.altindag.client.ClientType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static nl.altindag.client.ClientType.FEIGN_OK_HTTP_CLIENT;

@Service
public class FeignOkHttpClientService extends FeignService {

    public FeignOkHttpClientService(@Qualifier("feignWithOkHttpClient") Feign.Builder feign) {
        super(feign);
    }

    @Override
    public ClientType getClientType() {
        return FEIGN_OK_HTTP_CLIENT;
    }

}
