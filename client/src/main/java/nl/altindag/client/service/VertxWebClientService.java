package nl.altindag.client.service;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.TrustOptions;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.ssl.SSLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE;

@Service
public class VertxWebClientService implements RequestService {

    private static final int TIMEOUT_AMOUNT_IN_SECONDS = 5;

    private final WebClient client;

    public VertxWebClientService(@Autowired(required = false) SSLFactory sslFactory) {
        WebClientOptions clientOptions = new WebClientOptions();

        if (nonNull(sslFactory)) {
            clientOptions.setSsl(true);

            sslFactory.getKeyManagerFactory()
                    .map(KeyManagerFactoryOptions::new)
                    .ifPresent(clientOptions::setKeyCertOptions);
            sslFactory.getTrustManagerFactory()
                    .map(TrustManagerFactoryOptions::new)
                    .ifPresent(clientOptions::setTrustOptions);

            sslFactory.getCiphers().forEach(clientOptions::addEnabledCipherSuite);
            sslFactory.getProtocols().forEach(clientOptions::addEnabledSecureTransportProtocol);
        }

        client = WebClient.create(Vertx.vertx(), clientOptions);
    }

    @Override
    public ClientResponse executeRequest(String url) throws Exception {
        URI uri = URI.create(url);

        HttpResponse<Buffer> response = client.get(uri.getPort(), uri.getHost(), uri.getPath())
                .putHeader(HEADER_KEY_CLIENT_TYPE, getClientType().getValue())
                .send()
                .toCompletionStage()
                .toCompletableFuture()
                .get(TIMEOUT_AMOUNT_IN_SECONDS, TimeUnit.SECONDS);

        return new ClientResponse(response.bodyAsString(), response.statusCode());
    }

    @Override
    public ClientType getClientType() {
        return ClientType.VERTX;
    }

    private static class KeyManagerFactoryOptions implements KeyCertOptions {

        private final KeyManagerFactory keyManagerFactory;

        public KeyManagerFactoryOptions(KeyManagerFactory keyManagerFactory) {
            this.keyManagerFactory = keyManagerFactory;
        }

        private KeyManagerFactoryOptions(KeyManagerFactoryOptions other) {
            this.keyManagerFactory = other.keyManagerFactory;
        }

        @Override
        public KeyCertOptions copy() {
            return new KeyManagerFactoryOptions(this);
        }

        @Override
        public KeyManagerFactory getKeyManagerFactory(Vertx vertx) {
            return keyManagerFactory;
        }

        @Override
        public Function<String, X509KeyManager> keyManagerMapper(Vertx vertx) {
            return keyManagerFactory.getKeyManagers()[0] instanceof X509KeyManager ? serverName -> (X509KeyManager) keyManagerFactory.getKeyManagers()[0] : null;
        }

    }

    private static class TrustManagerFactoryOptions implements TrustOptions {

        private final TrustManagerFactory trustManagerFactory;

        public TrustManagerFactoryOptions(TrustManagerFactory trustManagerFactory) {
            this.trustManagerFactory = trustManagerFactory;
        }

        private TrustManagerFactoryOptions(TrustManagerFactoryOptions other) {
            trustManagerFactory = other.trustManagerFactory;
        }

        @Override
        public TrustOptions copy() {
            return new TrustManagerFactoryOptions(this);
        }

        @Override
        public TrustManagerFactory getTrustManagerFactory(Vertx vertx) {
            return trustManagerFactory;
        }

        @Override
        public Function<String, TrustManager[]> trustManagerMapper(Vertx vertx) {
            return serverName -> trustManagerFactory.getTrustManagers();
        }

    }

}
