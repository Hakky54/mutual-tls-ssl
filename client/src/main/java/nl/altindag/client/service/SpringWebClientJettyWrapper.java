package nl.altindag.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SpringWebClientJettyWrapper extends SpringWebClientWrapper {

    @Autowired
    public SpringWebClientJettyWrapper(WebClient webClientWithJetty) {
        super(webClientWithJetty);
    }

}
