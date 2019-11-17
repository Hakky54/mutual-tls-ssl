package nl.altindag.client;

public enum ClientType {

    APACHE_HTTP_CLIENT("apache httpclient"),
    JDK_HTTP_CLIENT("jdk httpclient"),
    OLD_JDK_HTTP_CLIENT("old jdk httpclient"),
    SPRING_REST_TEMPATE("spring resttemplate"),
    SPRING_WEB_CLIENT_NETTY("spring webflux webclient netty"),
    SPRING_WEB_CLIENT_JETTY("spring webflux webclient jetty"),
    OK_HTTP("okhttp"),
    JERSEY_CLIENT("jersey client"),
    OLD_JERSEY_CLIENT("old jersey client"),
    GOOGLE_HTTP_CLIENT("google httpclient"),
    UNIREST("unirest");

    private String value;

    ClientType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ClientType from(String value) {
        for (ClientType clientType : ClientType.values()) {
            if (clientType.getValue().equalsIgnoreCase(value)) {
                return clientType;
            }
        }

        throw new ClientException(String.format("Could not find the provided [%s] client type", value));
    }

}
