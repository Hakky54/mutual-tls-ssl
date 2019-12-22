package nl.altindag.client;

import org.springframework.stereotype.Component;

import nl.altindag.client.model.ClientResponse;

@Component
public class TestScenario {

    private ClientResponse clientResponse;
    private long beginTime;
    private long endTime;

    public ClientResponse getClientResponse() {
        return clientResponse;
    }

    public void setClientResponse(ClientResponse clientResponse) {
        this.clientResponse = clientResponse;
    }

    public void setStartTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getExecutionTimeInMilliSeconds() {
        return endTime - beginTime;
    }

}
