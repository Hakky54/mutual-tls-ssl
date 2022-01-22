package nl.altindag.client;

public enum ClientType {

    APACHE_HTTP_CLIENT("apache httpclient"),
    APACHE_HTTP_ASYNC_CLIENT("apache http async client"),
    APACHE5_HTTP_CLIENT("Apache 5 Http Client"),
    APACHE5_HTTP_ASYNC_CLIENT("Apache 5 Http Async Client"),
    JDK_HTTP_CLIENT("jdk httpclient"),
    OLD_JDK_HTTP_CLIENT("old jdk httpclient"),
    SPRING_REST_TEMPLATE("spring rest template"),
    SPRING_WEB_CLIENT_NETTY("spring webflux webclient netty"),
    SPRING_WEB_CLIENT_JETTY("spring webflux webclient jetty"),
    OK_HTTP("okhttp"),
    JERSEY_CLIENT("jersey httpclient"),
    OLD_JERSEY_CLIENT("old jersey httpclient"),
    APACHE_CXF_WEB_CLIENT("Apache CXF WebClient"),
    APACHE_CXF_JAX_RS("Apache CXF JAX-RS"),
    GOOGLE_HTTP_CLIENT("google httpclient"),
    UNIREST("unirest"),
    RETROFIT("retrofit"),
    FINAGLE("finagle"),
    AKKA_HTTP_CLIENT("akka httpclient"),
    DISPATCH_REBOOT_HTTP_CLIENT("dispatch reboot httpclient"),
    ASYNC_HTTP_CLIENT("async httpclient"),
    SCALAJ_HTTP_CLIENT("scalaj httpclient"),
    REACTOR_NETTY("reactor netty"),
    JETTY_REACTIVE_HTTP_CLIENT("jetty reactive httpclient"),
    FUEL("fuel"),
    STTP("sttp"),
    REQUESTS_SCALA("requests scala"),
    KOHTTP("kohttp"),
    HTTP4K_APACHE5_HTTP_CLIENT("http4k apache5 http client"),
    HTTP4K_APACHE5_ASYNC_HTTP_CLIENT("http4k apache5 async http client"),
    HTTP4K_APACHE4_HTTP_CLIENT("http4k apache4 http client"),
    HTTP4K_APACHE4_ASYNC_HTTP_CLIENT("http4k apache4 async http client"),
    HTTP4K_JAVA_HTTP_CLIENT("http4k java http client"),
    HTTP4K_JETTY_HTTP_CLIENT("http4k jetty http client"),
    HTTP4K_OK_HTTP_CLIENT("http4k okhttp client"),
    HTTP4S_BLAZE_CLIENT("http4s blaze client"),
    HTTP4S_JAVA_NET_CLIENT("http4s java net client"),
    FEIGN_APACHE_HTTP_CLIENT("feign apache httpclient"),
    FEIGN_APACHE5_HTTP_CLIENT("feign apache 5 httpclient"),
    FEIGN_GOOGLE_HTTP_CLIENT("feign google httpclient"),
    FEIGN_OLD_JDK_HTTP_CLIENT("feign old jdk http client"),
    FEIGN_JDK_HTTP_CLIENT("feign jdk http client"),
    FEIGN_OK_HTTP_CLIENT("feign okhttp client"),
    METHANOL("methanol"),
    KTOR_APACHE_HTTP_CLIENT("ktor apache httpclient"),
    KTOR_OK_HTTP("ktor okhttp httpclient"),
    KTOR_ANDROID_HTTP_CLIENT("ktor android httpclient"),
    KTOR_CIO_HTTP_CLIENT("ktor cio httpclient"),
    KTOR_JAVA_HTTP_CLIENT("ktor java httpclient"),
    VERTX("vertx webclient"),
    NONE("none");

    private final String value;

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
