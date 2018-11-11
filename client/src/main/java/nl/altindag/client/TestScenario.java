package nl.altindag.client;

import org.apache.http.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public class TestScenario {

    private HttpResponse response;

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

}
