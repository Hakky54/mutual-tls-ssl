package nl.altindag.server.util;

import nl.altindag.server.mapper.ApplicationPropertyMapper;
import nl.altindag.server.model.ApplicationProperty;

import java.io.IOException;
import java.util.Properties;

public final class ApplicationPropertyUtils {

    private ApplicationPropertyUtils() {}

    public static ApplicationProperty readApplicationProperties(String propertyPath) throws IOException {
        var classLoader = Thread.currentThread().getContextClassLoader();
        var properties = new Properties();
        try (var inputStream = classLoader.getResourceAsStream(propertyPath)) {
            properties.load(inputStream);
        }
        return ApplicationPropertyMapper.apply(properties);
    }

}
