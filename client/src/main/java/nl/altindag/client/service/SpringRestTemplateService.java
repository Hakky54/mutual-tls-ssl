package nl.altindag.client.service;

import static nl.altindag.client.ClientType.SPRING_REST_TEMPLATE;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

@Service
public class SpringRestTemplateService implements RequestService {

    private final RestTemplate restTemplate;

    public SpringRestTemplateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ClientResponse executeRequest(String url) {
        var headers = new HttpHeaders();
        headers.add(HEADER_KEY_CLIENT_TYPE, getClientType().getValue());
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return new ClientResponse(response.getBody(), response.getStatusCodeValue());
    }

    @Override
    public ClientType getClientType() {
        return SPRING_REST_TEMPLATE;
    }
}
