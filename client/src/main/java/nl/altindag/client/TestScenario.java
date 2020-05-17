package nl.altindag.client;

import org.springframework.stereotype.Component;

import nl.altindag.client.model.ClientResponse;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class TestScenario {

    private ClientResponse clientResponse;
    private LocalTime beginTime;
    private LocalTime endTime;

    public ClientResponse getClientResponse() {
        return clientResponse;
    }

    public void setClientResponse(ClientResponse clientResponse) {
        this.clientResponse = clientResponse;
    }

    public void setStartTime(LocalTime beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public long getExecutionTimeInMilliSeconds() {
        return Duration.between(beginTime, endTime).toMillis();
    }

}
