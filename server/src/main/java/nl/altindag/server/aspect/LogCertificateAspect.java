package nl.altindag.server.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class LogCertificateAspect {

    private static final Logger LOGGER = LogManager.getLogger(LogCertificateAspect.class);
    private static final String KEY_CERTIFICATE_ATTRIBUTE = "javax.servlet.request.X509Certificate";

    @Before("@annotation(nl.altindag.server.aspect.LogCertificate)")
    public void logCertificateIfPresent(JoinPoint joinPoint) {
        getCertificatesFromRequest()
                .map(Arrays::stream)
                .flatMap(Stream::findFirst)
                .map(X509Certificate::getSubjectDN)
                .map(Principal::getName)
                .ifPresent(certificate -> LOGGER.info("Received the following certificate details: " + certificate));
    }

    private Optional<X509Certificate[]> getCertificatesFromRequest() {
        return Optional.ofNullable((X509Certificate[]) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getAttribute(KEY_CERTIFICATE_ATTRIBUTE));
    }
}
