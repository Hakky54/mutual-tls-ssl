package nl.altindag.server.aspect;

import static java.util.Objects.nonNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class LogClientTypeAspect {

    private static final Logger LOGGER = LogManager.getLogger(LogClientTypeAspect.class);
    private static final String HEADER_KEY_CLIENT_TYPE = "client-type";

    @Before("@annotation(nl.altindag.server.aspect.LogClientType)")
    public void logClientTypeIfPresent() {
        String clientType = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getHeader(HEADER_KEY_CLIENT_TYPE);

        if (nonNull(clientType)) {
            LOGGER.info("Received the request from the following client: {}", clientType);
        }
    }

}
