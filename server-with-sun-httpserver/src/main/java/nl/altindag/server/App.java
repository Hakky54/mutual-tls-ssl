package nl.altindag.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import nl.altindag.server.controller.HelloWorldController;
import nl.altindag.server.model.ApplicationProperty;
import nl.altindag.server.util.ApplicationPropertyUtils;
import nl.altindag.server.util.SSLFactoryUtils;
import nl.altindag.ssl.SSLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.nonNull;

@SuppressWarnings({"unused", "java:S1068"})
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String APPLICATION_PROPERTIES_WITHOUT_AUTHENTICATION = "application-without-authentication.properties";
    private static final String APPLICATION_PROPERTIES_WITH_ONE_WAY_AUTHENTICATION = "application-one-way-authentication.properties";
    private static final String APPLICATION_PROPERTIES_WITH_TWO_WAY_AUTHENTICATION = "application-two-way-authentication.properties";

    private static HttpServer httpServer = null;
    private static ExecutorService executorService = null;

    private App() {}

    public static void main(String[] args) throws IOException {
        String defaultPropertiesPath = System.getProperty("properties", APPLICATION_PROPERTIES_WITHOUT_AUTHENTICATION);

        ApplicationProperty applicationProperty = ApplicationPropertyUtils.readApplicationProperties(defaultPropertiesPath);
        executorService = Executors.newCachedThreadPool();

        httpServer = createServer(applicationProperty, executorService);
        httpServer.start();
    }

    private static HttpServer createServer(ApplicationProperty applicationProperty, Executor executorService) throws IOException {
        LOGGER.debug("Loading the following application properties: [{}]", applicationProperty);

        HttpServer httpServer;
        InetSocketAddress socketAddress = new InetSocketAddress(Integer.parseInt(applicationProperty.getServerPort()));

        if (applicationProperty.isSslEnabled()) {
            SSLFactory sslFactory = SSLFactoryUtils.createSSLFactory(applicationProperty);

            HttpsServer httpsServer = HttpsServer.create(socketAddress, 0);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslFactory.getSslContext()) {
                @Override
                public void configure(HttpsParameters params) {
                    params.setSSLParameters(sslFactory.getSslParameters());
                    params.setNeedClientAuth(applicationProperty.isSslClientAuth());
                }
            });

            httpServer = httpsServer;
        } else {
            httpServer = HttpServer.create(socketAddress, 0);
        }

        httpServer.createContext("/api/hello", new HelloWorldController());
        httpServer.setExecutor(executorService);
        return httpServer;
    }

    static void stopServerIfRunning() {
        if (nonNull(httpServer) && nonNull(executorService)) {
            httpServer.stop(0);
            executorService.shutdownNow();

            executorService = null;
            httpServer = null;
        }
    }

}
