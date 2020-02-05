package nl.altindag.server.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import nl.altindag.log.LogCaptor;

@RunWith(MockitoJUnitRunner.class)
public class LogCertificateAspectShould {

    private LogCertificateAspect logCertificateAspect = new LogCertificateAspect();

    @Test
    public void logDetailedClientCertificate() {
        LogCaptor logCaptor = LogCaptor.forClass(LogCertificateAspect.class);

        X509Certificate x509Certificate = mock(X509Certificate.class);
        X509Certificate[] x509Certificates = new X509Certificate[]{x509Certificate};

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.request.X509Certificate", x509Certificates);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(x509Certificate.toString()).thenReturn("this is a dummy detailed certificate");

        LogCertificate logCertificate = createLogCertificate(true);
        logCertificateAspect.logCertificateIfPresent(logCertificate);

        List<String> logs = logCaptor.getLogs();
        assertThat(logs).containsExactly("Received the following certificate details: this is a dummy detailed certificate");
    }

    @Test
    public void logLessDetailedClientCertificate() {
        LogCaptor logCaptor = LogCaptor.forClass(LogCertificateAspect.class);

        X509Certificate x509Certificate = mock(X509Certificate.class);
        X509Certificate[] x509Certificates = new X509Certificate[]{x509Certificate};
        X500Principal x500Principal = mock(X500Principal.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.request.X509Certificate", x509Certificates);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(x509Certificate.getSubjectX500Principal()).thenReturn(x500Principal);
        when(x500Principal.getName()).thenReturn("cn=hakan");

        LogCertificate logCertificate = createLogCertificate(false);
        logCertificateAspect.logCertificateIfPresent(logCertificate);

        List<String> logs = logCaptor.getLogs();
        assertThat(logs).containsExactly("Received the following certificate details: cn=hakan");
    }

    @Test
    public void notLogCertificateWhenNotPresent() {
        LogCaptor logCaptor = LogCaptor.forClass(LogCertificateAspect.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        LogCertificate logCertificate = createLogCertificate(true);
        logCertificateAspect.logCertificateIfPresent(logCertificate);

        List<String> logs = logCaptor.getLogs();
        assertThat(logs).isEmpty();
    }

    private LogCertificate createLogCertificate(boolean detailed) {
        return new LogCertificate() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public boolean detailed() {
                return detailed;
            }
        };
    }

}
