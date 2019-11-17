package nl.altindag.client.service;

import static nl.altindag.client.ClientType.SPRING_REST_TEMPATE;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_KEY_CLIENT_TYPE, SPRING_REST_TEMPATE.getValue());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return new ClientResponse(response.getBody(), response.getStatusCodeValue());
    }

}
