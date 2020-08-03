package nl.altindag.server.mapper;

import nl.altindag.server.model.ApplicationProperty;

import java.util.Properties;

public class ApplicationPropertyMapper {

    private static final String SERVER_PORT = "server.port";
    private static final String SSL_ENABLED = "ssl.enabled";
    private static final String SSL_CLIENT_AUTH = "ssl.client-auth";
    private static final String KEYSTORE_PATH = "ssl.keystore-path";
    private static final String KEYSTORE_PASSWORD = "ssl.keystore-password";
    private static final String TRUSTSTORE_PATH = "ssl.truststore-path";
    private static final String TRUSTSTORE_PASSWORD = "ssl.truststore-password";

    private ApplicationPropertyMapper() {}

    public static ApplicationProperty apply(Properties properties) {
        ApplicationProperty applicationProperty = new ApplicationProperty();
        applicationProperty.setServerHttpPort(properties.getProperty(SERVER_PORT));
        applicationProperty.setSslEnabled(Boolean.parseBoolean(properties.getProperty(SSL_ENABLED, Boolean.FALSE.toString())));
        applicationProperty.setSslClientAuth(Boolean.parseBoolean(properties.getProperty(SSL_CLIENT_AUTH, Boolean.FALSE.toString())));
        applicationProperty.setKeystorePath(properties.getProperty(KEYSTORE_PATH));
        applicationProperty.setKeystorePassword(properties.getProperty(KEYSTORE_PASSWORD, "").toCharArray());
        applicationProperty.setTruststorePath(properties.getProperty(TRUSTSTORE_PATH));
        applicationProperty.setTruststorePassword(properties.getProperty(TRUSTSTORE_PASSWORD, "").toCharArray());
        return applicationProperty;
    }

}
