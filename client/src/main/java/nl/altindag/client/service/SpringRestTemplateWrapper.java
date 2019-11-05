package nl.altindag.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import nl.altindag.client.model.ClientResponse;

@Service
public class SpringRestTemplateWrapper extends RequestService {

    private final RestTemplate restTemplate;

    @Autowired
    public SpringRestTemplateWrapper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        return new ClientResponse(response.getBody(), response.getStatusCodeValue());
    }

}
