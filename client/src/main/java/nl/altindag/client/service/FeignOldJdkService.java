package nl.altindag.client.service;

import feign.Feign;
import nl.altindag.client.ClientType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static nl.altindag.client.ClientType.FEIGN_OLD_JDK_HTTP_CLIENT;

@Service
public class FeignOldJdkService extends FeignService {

    public FeignOldJdkService(@Qualifier("feignWithOldJdkHttpClient") Feign.Builder feign) {
        super(feign);
    }

    @Override
    public ClientType getClientType() {
        return FEIGN_OLD_JDK_HTTP_CLIENT;
    }

}
