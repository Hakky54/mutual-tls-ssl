package nl.altindag.server.util;

import nl.altindag.server.model.ApplicationProperty;
import nl.altindag.ssl.SSLFactory;

import static java.util.Objects.nonNull;

public final class SSLFactoryUtils {

    private SSLFactoryUtils() {}

    public static SSLFactory createSSLFactory(ApplicationProperty applicationProperty) {
        var sslFactoryBuilder = SSLFactory.builder()
                .withIdentityMaterial(applicationProperty.getKeystorePath(), applicationProperty.getKeystorePassword())
                .withDefaultTrustMaterial();

        if (nonNull(applicationProperty.getTruststorePath())) {
            sslFactoryBuilder.withTrustMaterial(applicationProperty.getTruststorePath(), applicationProperty.getTruststorePassword());
        }

        return sslFactoryBuilder.build();
    }

}
