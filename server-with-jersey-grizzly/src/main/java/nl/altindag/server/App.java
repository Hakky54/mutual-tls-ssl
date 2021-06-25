package nl.altindag.server;

import nl.altindag.server.controller.HelloWorldController;
import nl.altindag.server.model.ApplicationProperty;
import nl.altindag.server.util.ApplicationPropertyUtils;
import nl.altindag.server.util.SSLFactoryUtils;
import nl.altindag.ssl.SSLFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

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

        var applicationProperty = ApplicationPropertyUtils.readApplicationProperties(defaultPropertiesPath);
        httpServer = startServer(applicationProperty);
    }

    static void stopServerIfRunning() {
        if (Objects.nonNull(httpServer)) {
            httpServer.shutdownNow();
            httpServer = null;
        }
    }

    private static HttpServer startServer(ApplicationProperty applicationProperty) {
        LOGGER.debug("Loading the following application properties: [{}]", applicationProperty);

        var resourceConfig = new ResourceConfig().packages(HelloWorldController.class.getPackageName());
        var baseUrl = String.format("http://localhost:%s/api", applicationProperty.getServerPort());

        if (applicationProperty.isSslEnabled()) {
            baseUrl = String.format("https://localhost:%s/api", applicationProperty.getServerPort());
            var sslFactory = SSLFactoryUtils.createSSLFactory(applicationProperty);

            var sslEngineConfigurator = new SSLEngineConfigurator(
                    sslFactory.getSslContext(),
                    false,
                    applicationProperty.isSslClientAuth(),
                    applicationProperty.isSslClientAuth()
            );

            return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig, true, sslEngineConfigurator);
        }

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), resourceConfig);
    }

}

