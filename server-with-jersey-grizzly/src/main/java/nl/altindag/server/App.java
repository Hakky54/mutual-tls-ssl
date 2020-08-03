package nl.altindag.server;

import nl.altindag.server.controller.HelloWorldController;
import nl.altindag.server.mapper.ApplicationPropertyMapper;
import nl.altindag.server.model.ApplicationProperty;
import nl.altindag.sslcontext.SSLFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import static java.util.Objects.nonNull;

@SuppressWarnings("unused")
public class App {

    private static final String APPLICATION_PROPERTIES_WITHOUT_AUTHENTICATION = "application-without-authentication.properties";
    private static final String APPLICATION_PROPERTIES_WITH_ONE_WAY_AUTHENTICATION = "application-one-way-authentication.properties";
    private static final String APPLICATION_PROPERTIES_WITH_TWO_WAY_AUTHENTICATION = "application-two-way-authentication.properties";
    private static final String DEFAULT_PROPERTIES = APPLICATION_PROPERTIES_WITHOUT_AUTHENTICATION;

    public static void main(String[] args) throws IOException {
        ApplicationProperty applicationProperty = readApplicationProperties();
        startServer(applicationProperty);
    }

    private static ApplicationProperty readApplicationProperties() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try (InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_PROPERTIES)) {
            properties.load(inputStream);
        }
        return ApplicationPropertyMapper.apply(properties);
    }

    private static HttpServer startServer(ApplicationProperty applicationProperty) {
        ResourceConfig resourceConfig = new ResourceConfig().packages(HelloWorldController.class.getPackageName());
        String baseUrl = String.format("http://localhost:%s/api", applicationProperty.getServerHttpPort());

        if (applicationProperty.isSslEnabled()) {
            baseUrl = String.format("https://localhost:%s/api", applicationProperty.getServerHttpsPort());
            SSLFactory sslFactory = createSSLFactory(applicationProperty);

            SSLEngineConfigurator sslEngineConfigurator = new SSLEngineConfigurator(
                    sslFactory.getSslContext(),
                    false,
                    applicationProperty.isSslClientAuth(),
                    applicationProperty.isSslClientAuth()
            );

            return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig, true, sslEngineConfigurator);
        }

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig);
    }

    private static SSLFactory createSSLFactory(ApplicationProperty applicationProperty) {
        SSLFactory.Builder sslFactoryBuilder = SSLFactory.builder();
        if (nonNull(applicationProperty.getKeystorePath())) {
            sslFactoryBuilder.withIdentityMaterial(applicationProperty.getKeystorePath(), applicationProperty.getKeystorePassword());
        }

        if (nonNull(applicationProperty.getTruststorePath())) {
            sslFactoryBuilder.withIdentityMaterial(applicationProperty.getTruststorePath(), applicationProperty.getTruststorePassword());
        }

        return sslFactoryBuilder.build();
    }

}

