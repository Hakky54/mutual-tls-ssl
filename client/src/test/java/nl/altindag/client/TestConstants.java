package nl.altindag.client;

public class TestConstants {

    public static final String HTTP_URL = "http://localhost:8080/api/hello";
    public static final String HTTPS_URL = "https://localhost:8443/api/hello";

    public static final String HEADER_KEY_CLIENT_TYPE = "client-type";

    public static final String APACHE_HTTP_CLIENT = "apache httpclient";
    public static final String JDK_HTTP_CLIENT = "jdk httpclient";
    public static final String OLD_JDK_HTTP_CLIENT = "old jdk httpclient";
    public static final String SPRING_REST_TEMPATE = "spring resttemplate";
    public static final String SPRING_WEB_CLIENT_NETTY = "spring webflux webclient netty";
    public static final String SPRING_WEB_CLIENT_JETTY = "spring webflux webclient jetty";
    public static final String OK_HTTP = "okhttp";
    public static final String JERSEY_CLIENT = "jersey client";
    public static final String OLD_JERSEY_CLIENT = "old jersey client";
    public static final String GOOGLE_HTTP_CLIENT = "google httpclient";

    public static final String GET_METHOD = "GET";

    private TestConstants() {}

}
