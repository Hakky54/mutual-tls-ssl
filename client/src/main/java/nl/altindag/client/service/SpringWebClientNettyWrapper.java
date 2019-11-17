package nl.altindag.client.service;

import static nl.altindag.client.ClientType.SPRING_WEB_CLIENT_NETTY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import nl.altindag.client.ClientType;

@Service
public class SpringWebClientNettyWrapper extends SpringWebClientWrapper {

    @Autowired
    public SpringWebClientNettyWrapper(WebClient webClientWithNetty) {
        super(webClientWithNetty);
    }

    @Override
    protected ClientType getClientType() {
        return SPRING_WEB_CLIENT_NETTY;
    }

}
