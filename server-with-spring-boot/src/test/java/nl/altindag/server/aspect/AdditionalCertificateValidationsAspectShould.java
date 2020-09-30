package nl.altindag.server.aspect;

import nl.altindag.log.LogCaptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.x500.X500Principal;
import java.lang.annotation.Annotation;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("rawtypes")
@ExtendWith(MockitoExtension.class)
class AdditionalCertificateValidationsAspectShould {

    AdditionalCertificateValidationsAspect victim = new AdditionalCertificateValidationsAspect();

    @Test
    void validateCertificateForAllowedCommonNameContinuesTheFlowWithProceedingJoinPoint() throws Throwable {
        var proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        var additionalCertificateValidations = createAdditionalCertificateValidations();

        X509Certificate x509Certificate = mock(X509Certificate.class);
        X509Certificate[] x509Certificates = new X509Certificate[]{x509Certificate};
        X500Principal x500Principal = mock(X500Principal.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.request.X509Certificate", x509Certificates);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(x509Certificate.getSubjectX500Principal()).thenReturn(x500Principal);
        when(x500Principal.getName()).thenReturn("CN=black-hole,OU=Altindag,O=Altindag,C=NL");

        victim.validate(proceedingJoinPoint, additionalCertificateValidations);

        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void validateCertificateForNotAllowedCommonNameReturnsBadRequestHttpStatus() throws Throwable {
        var proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        var additionalCertificateValidations = createAdditionalCertificateValidations();

        X509Certificate x509Certificate = mock(X509Certificate.class);
        X509Certificate[] x509Certificates = new X509Certificate[]{x509Certificate};
        X500Principal x500Principal = mock(X500Principal.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.request.X509Certificate", x509Certificates);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(x509Certificate.getSubjectX500Principal()).thenReturn(x500Principal);
        when(x500Principal.getName()).thenReturn("CN=white-tower,OU=Altindag,O=Altindag,C=NL");

        Object response = victim.validate(proceedingJoinPoint, additionalCertificateValidations);

        verify(proceedingJoinPoint, times(0)).proceed();
        assertThat(response).isInstanceOf(ResponseEntity.class);

        ResponseEntity responseEntity = (ResponseEntity) response;
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(400);
        assertThat(responseEntity.getBody()).isEqualTo("This certificate is not a valid one");
    }

    @Test
    void validateCertificateForUnrecognizedCommonNameReturnsBadRequestHttpStatus() throws Throwable {
        var proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        var additionalCertificateValidations = createAdditionalCertificateValidations();

        X509Certificate x509Certificate = mock(X509Certificate.class);
        X509Certificate[] x509Certificates = new X509Certificate[]{x509Certificate};
        X500Principal x500Principal = mock(X500Principal.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.request.X509Certificate", x509Certificates);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(x509Certificate.getSubjectX500Principal()).thenReturn(x500Principal);
        when(x500Principal.getName()).thenReturn("qwertyuiop");

        Object response = victim.validate(proceedingJoinPoint, additionalCertificateValidations);

        verify(proceedingJoinPoint, times(0)).proceed();
        assertThat(response).isInstanceOf(ResponseEntity.class);

        ResponseEntity responseEntity = (ResponseEntity) response;
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(400);
        assertThat(responseEntity.getBody()).isEqualTo("This certificate is not a valid one");
    }

    @Test
    void ignoreCertificateValidationWhenThereIsNoCertificate() throws Throwable {
        LogCaptor logCaptor = LogCaptor.forClass(AdditionalCertificateValidationsAspect.class);

        var proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        var additionalCertificateValidations = createAdditionalCertificateValidations();

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        victim.validate(proceedingJoinPoint, additionalCertificateValidations);

        verify(proceedingJoinPoint, times(1)).proceed();
        assertThat(logCaptor.getInfoLogs()).containsExactly("Skipping common name validation because certificate is not present within the request");
    }

    private AdditionalCertificateValidations createAdditionalCertificateValidations() {
        return new AdditionalCertificateValidations() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String[] allowedCommonNames() {
                return new String[]{"black-hole"};
            }

            @Override
            public String[] notAllowedCommonNames() {
                return new String[]{"white-tower"};
            }
        };
    }
}
