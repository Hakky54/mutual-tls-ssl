package nl.altindag.server.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class AdditionalCertificateValidationsAspect {

    private static final String KEY_CERTIFICATE_ATTRIBUTE = "javax.servlet.request.X509Certificate";
    private static final Pattern COMMON_NAME_PATTERN = Pattern.compile("(?<=CN=)(.*?)(?=,)");

    @Around("@annotation(certificateValidations)")
    public Object validate(ProceedingJoinPoint joinPoint,
                           AdditionalCertificateValidations certificateValidations) throws Throwable {

        List<String> allowedCommonNames = Arrays.asList(certificateValidations.allowedCommonNames());
        List<String> notAllowedCommonNames = Arrays.asList(certificateValidations.notAllowedCommonNames());

        Optional<String> allowedCommonName = getCommonNameFromCertificate()
                .filter(commonName -> allowedCommonNames.isEmpty() || allowedCommonNames.contains(commonName))
                .filter(commonName -> notAllowedCommonNames.isEmpty() || !notAllowedCommonNames.contains(commonName));

        if (allowedCommonName.isPresent()) {
            return joinPoint.proceed();
        } else {
            return ResponseEntity.badRequest().body("This certificate is not a valid one");
        }
    }

    private Optional<String> getCommonNameFromCertificate() {
        return getCertificatesFromRequest()
                .map(Arrays::stream)
                .flatMap(Stream::findFirst)
                .map(X509Certificate::getSubjectX500Principal)
                .map(X500Principal::getName)
                .flatMap(this::getCommonName);
    }

    private Optional<X509Certificate[]> getCertificatesFromRequest() {
        return Optional.ofNullable((X509Certificate[]) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getAttribute(KEY_CERTIFICATE_ATTRIBUTE));
    }

    private Optional<String> getCommonName(String subjectDistinguishedName) {
        Matcher matcher = COMMON_NAME_PATTERN.matcher(subjectDistinguishedName);

        if (matcher.find()) {
            return Optional.of(matcher.group());
        } else {
            return Optional.empty();
        }
    }

}
