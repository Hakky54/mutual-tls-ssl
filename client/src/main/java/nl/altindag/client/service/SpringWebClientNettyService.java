package nl.altindag.client.service;

import static nl.altindag.client.ClientType.SPRING_WEB_CLIENT_NETTY;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import nl.altindag.client.ClientType;

@Service
public class SpringWebClientNettyService extends SpringWebClientService {

    public SpringWebClientNettyService(WebClient webClientWithNetty) {
        super(webClientWithNetty);
    }

    @Override
    public ClientType getClientType() {
        return SPRING_WEB_CLIENT_NETTY;
    }

}
