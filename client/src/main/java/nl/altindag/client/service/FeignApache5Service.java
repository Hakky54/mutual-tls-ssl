package nl.altindag.client.service;

import feign.Feign;
import nl.altindag.client.ClientType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static nl.altindag.client.ClientType.FEIGN_APACHE5_HTTP_CLIENT;

@Service
public class FeignApache5Service extends FeignService {

    public FeignApache5Service(@Qualifier("feignWithApache5HttpClient") Feign.Builder feign) {
        super(feign);
    }

    @Override
    public ClientType getClientType() {
        return FEIGN_APACHE5_HTTP_CLIENT;
    }

}
