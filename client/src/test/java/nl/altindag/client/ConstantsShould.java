package nl.altindag.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ConstantsShould {

    @Test
    public void returnClientType() {
        assertThat(Constants.HEADER_KEY_CLIENT_TYPE).isEqualTo(TestConstants.HEADER_KEY_CLIENT_TYPE);
    }

    @Test
    public void returnApacheHttpClient() {
        assertThat(Constants.APACHE_HTTP_CLIENT).isEqualTo(TestConstants.APACHE_HTTP_CLIENT);
    }

    @Test
    public void returnJdkHttpClient() {
        assertThat(Constants.JDK_HTTP_CLIENT).isEqualTo(TestConstants.JDK_HTTP_CLIENT);
    }

    @Test
    public void returnOldJdkHttpClient() {
        assertThat(Constants.OLD_JDK_HTTP_CLIENT).isEqualTo(TestConstants.OLD_JDK_HTTP_CLIENT);
    }

    @Test
    public void returnSpringRestTemplate() {
        assertThat(Constants.SPRING_REST_TEMPATE).isEqualTo(TestConstants.SPRING_REST_TEMPATE);
    }

    @Test
    public void returnSpringWebClientNetty() {
        assertThat(Constants.SPRING_WEB_CLIENT_NETTY).isEqualTo(TestConstants.SPRING_WEB_CLIENT_NETTY);
    }

    @Test
    public void returnSpringWebClientJetty() {
        assertThat(Constants.SPRING_WEB_CLIENT_JETTY).isEqualTo(TestConstants.SPRING_WEB_CLIENT_JETTY);
    }

    @Test
    public void returnOkHttp() {
        assertThat(Constants.OK_HTTP).isEqualTo(TestConstants.OK_HTTP);
    }

    @Test
    public void returnJerseyClient() {
        assertThat(Constants.JERSEY_CLIENT).isEqualTo(TestConstants.JERSEY_CLIENT);
    }

    @Test
    public void returnOldJerseyClient() {
        assertThat(Constants.OLD_JERSEY_CLIENT).isEqualTo(TestConstants.OLD_JERSEY_CLIENT);
    }

}
