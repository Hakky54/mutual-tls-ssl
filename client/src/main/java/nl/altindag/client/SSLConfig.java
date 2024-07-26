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
package nl.altindag.client;

import nl.altindag.ssl.SSLFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * The SSLConfig class contains a Spring Bean to construct the SSL material
 * based on the given input with the library SSLContext Kickstart.
 * The library is a lightweight high level library to provide
 * convenient methods to easily construct the ssl material with
 * different kinds of input to configure over 40+ http clients.
 *
 * @see <a href="https://github.com/Hakky54/sslcontext-kickstart">
 *      https://github.com/Hakky54/sslcontext-kickstart
 *      </a>
 * @see <a href="https://github.com/Hakky54/sslcontext-kickstart#tested-http-clients">
 *      https://github.com/Hakky54/sslcontext-kickstart#tested-http-clients
 *      </a>
 */
@Component
public class SSLConfig {

    @Bean
    public SSLFactory sslFactory(
            @Value("${client.ssl.one-way-authentication-enabled:false}") boolean oneWayAuthenticationEnabled,
            @Value("${client.ssl.two-way-authentication-enabled:false}") boolean twoWayAuthenticationEnabled,
            @Value("${client.ssl.key-store:}") String keyStorePath,
            @Value("${client.ssl.key-store-password:}") char[] keyStorePassword,
            @Value("${client.ssl.trust-store:}") String trustStorePath,
            @Value("${client.ssl.trust-store-password:}") char[] trustStorePassword) {
        if (oneWayAuthenticationEnabled) {
            return SSLFactory.builder()
                    .withTrustMaterial(trustStorePath, trustStorePassword)
                    .build();
        } else if (twoWayAuthenticationEnabled) {
            return SSLFactory.builder()
                    .withIdentityMaterial(keyStorePath, keyStorePassword)
                    .withTrustMaterial(trustStorePath, trustStorePassword)
                    .build();
        } else {
            return SSLFactory.builder()
                    .withDefaultTrustMaterial()
                    .build();
        }
    }

}
