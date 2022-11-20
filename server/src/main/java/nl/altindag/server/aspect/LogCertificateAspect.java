/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.altindag.server.aspect;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.security.auth.x500.X500Principal;

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

        certificateDetails.ifPresent(certificate -> LOGGER.info("Received the following certificate details: {}", certificate));
    }

    private Optional<String> getPublicCertificate() {
        return getCertificatesFromRequest()
                .map(Arrays::stream)
                .flatMap(Stream::findFirst)
                .map(Certificate::toString);
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
