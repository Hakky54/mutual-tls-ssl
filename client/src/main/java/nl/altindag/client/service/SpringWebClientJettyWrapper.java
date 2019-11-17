package nl.altindag.client.service;

import static nl.altindag.client.ClientType.SPRING_WEB_CLIENT_JETTY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import nl.altindag.client.ClientType;

@Service
public class SpringWebClientJettyWrapper extends SpringWebClientWrapper {

    @Autowired
    public SpringWebClientJettyWrapper(WebClient webClientWithJetty) {
        super(webClientWithJetty);
    }

    @Override
    protected ClientType getClientType() {
        return SPRING_WEB_CLIENT_JETTY;
    }

}
