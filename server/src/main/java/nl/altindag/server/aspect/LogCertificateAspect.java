package nl.altindag.server.aspect;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.beanutils.ConvertUtils;
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
public class LogCertificateAspect {

    private static final Logger LOGGER = LogManager.getLogger(LogCertificateAspect.class);
    private static final String KEY_CERTIFICATE_ATTRIBUTE = "javax.servlet.request.X509Certificate";

    @Before("@annotation(logCertificate)")
    public void logCertificateIfPresent(LogCertificate logCertificate) {
        Optional<String> certificateDetails;
        if (logCertificate.detailed()) {
            certificateDetails = getPublicCertificate();
        } else {
            certificateDetails = getSubjectDistinguishedName();
        }

        certificateDetails.ifPresent(certificate -> LOGGER.info("Received the following certificate details: " + certificate));
    }

    private Optional<String> getPublicCertificate() {
        return getCertificatesFromRequest()
                .map(Arrays::stream)
                .flatMap(Stream::findFirst)
                .map(ConvertUtils::convert);
    }

    private Optional<String> getSubjectDistinguishedName() {
        return getCertificatesFromRequest()
                .map(Arrays::stream)
                .flatMap(Stream::findFirst)
                .map(X509Certificate::getSubjectX500Principal)
                .map(X500Principal::getName);
    }

    private Optional<X509Certificate[]> getCertificatesFromRequest() {
        return Optional.ofNullable((X509Certificate[]) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getAttribute(KEY_CERTIFICATE_ATTRIBUTE));
    }

}
