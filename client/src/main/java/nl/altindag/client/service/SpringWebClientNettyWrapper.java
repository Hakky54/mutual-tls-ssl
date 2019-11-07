package nl.altindag.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SpringWebClientNettyWrapper extends SpringWebClientWrapper {

    @Autowired
    public SpringWebClientNettyWrapper(WebClient webClientWithNetty) {
        super(webClientWithNetty);
    }

}
