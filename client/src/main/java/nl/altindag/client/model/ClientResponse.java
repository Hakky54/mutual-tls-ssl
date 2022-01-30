package nl.altindag.client.model;

public class ClientResponse {

    private final String responseBody;
    private final int statusCode;

    public ClientResponse(String responseBody, int statusCode) {
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

}
