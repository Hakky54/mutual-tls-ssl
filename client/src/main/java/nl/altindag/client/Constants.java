package nl.altindag.client;

import java.util.Optional;

public final class Constants {

    public static final String SERVER_URL = Optional.ofNullable(System.getProperty("url")).orElse("http://localhost:8080");
    public static final String HELLO_ENDPOINT = "/api/hello";
    public static final String HEADER_KEY_CLIENT_TYPE = "client-type";

    private Constants() {}

}
