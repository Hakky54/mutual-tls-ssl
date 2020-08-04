package nl.altindag.server;

import nl.altindag.server.controller.HelloWorldController;
import nl.altindag.server.mapper.ApplicationPropertyMapper;
import nl.altindag.server.model.ApplicationProperty;
import nl.altindag.sslcontext.SSLFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;

import static java.util.Objects.nonNull;

@SuppressWarnings({"unused", "java:S1068"})
public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String APPLICATION_PROPERTIES_WITHOUT_AUTHENTICATION = "application-without-authentication.properties";
    private static final String APPLICATION_PROPERTIES_WITH_ONE_WAY_AUTHENTICATION = "application-one-way-authentication.properties";
    private static final String APPLICATION_PROPERTIES_WITH_TWO_WAY_AUTHENTICATION = "application-two-way-authentication.properties";

    private static HttpServer httpServer = null;

    private App() {}

    public static void main(String[] args) throws IOException {
        String defaultPropertiesPath = System.getProperty("properties", APPLICATION_PROPERTIES_WITHOUT_AUTHENTICATION);

        ApplicationProperty applicationProperty = readApplicationProperties(defaultPropertiesPath);
        httpServer = startServer(applicationProperty);
    }

    private static ApplicationProperty readApplicationProperties(String propertyPath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try (InputStream inputStream = classLoader.getResourceAsStream(propertyPath)) {
            properties.load(inputStream);
        }
        return ApplicationPropertyMapper.apply(properties);
    }

    static void stopServerIfRunning() {
        if (Objects.nonNull(httpServer)) {
            httpServer.shutdownNow();
            httpServer = null;
        }
    }

    private static HttpServer startServer(ApplicationProperty applicationProperty) {
        LOGGER.debug("Loading the following application properties: [{}]", applicationProperty);

        ResourceConfig resourceConfig = new ResourceConfig().packages(HelloWorldController.class.getPackageName());
        String baseUrl = String.format("http://localhost:%s/api", applicationProperty.getServerPort());

        if (applicationProperty.isSslEnabled()) {
            baseUrl = String.format("https://localhost:%s/api", applicationProperty.getServerPort());
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
            sslFactoryBuilder.withTrustMaterial(applicationProperty.getTruststorePath(), applicationProperty.getTruststorePassword());
        }

        return sslFactoryBuilder.build();
    }

}

