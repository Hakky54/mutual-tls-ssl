package nl.altindag.client.service;

import static nl.altindag.client.ClientType.SPRING_WEB_CLIENT_JETTY;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import nl.altindag.client.ClientType;

@Service
public class SpringWebClientJettyService extends SpringWebClientService {

    public SpringWebClientJettyService(WebClient webClientWithJetty) {
        super(webClientWithJetty);
    }

    @Override
    public ClientType getClientType() {
        return SPRING_WEB_CLIENT_JETTY;
    }

}
