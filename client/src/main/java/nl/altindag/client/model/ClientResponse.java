package nl.altindag.client.model;

public class ClientResponse {

    private String responseBody;
    private int statusCode;

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
