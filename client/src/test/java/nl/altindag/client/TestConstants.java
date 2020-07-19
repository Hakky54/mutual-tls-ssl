package nl.altindag.client;

public class TestConstants {

    public static final String HTTP_SERVER_URL = "http://localhost:8080";
    public static final String HTTPS_SERVER_URL = "https://localhost:8443";
    public static final String SERVER_HELLO_ENDPOINT = "/api/hello";
    public static final String HTTP_URL = HTTP_SERVER_URL + SERVER_HELLO_ENDPOINT;
    public static final String HTTPS_URL = HTTPS_SERVER_URL + SERVER_HELLO_ENDPOINT;

    public static final String HEADER_KEY_CLIENT_TYPE = "client-type";
    public static final String GET_METHOD = "GET";

    private TestConstants() {}

}
