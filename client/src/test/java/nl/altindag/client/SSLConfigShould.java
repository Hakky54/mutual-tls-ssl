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
import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;

class SSLConfigShould {

    private final SSLConfig victim = new SSLConfig();

    @Test
    void createSslFactoryWithOneWayAuthentication() {
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLFactory sslFactory = victim.sslFactory(true, false,
                EMPTY, EMPTY.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslFactory).isNotNull();
        assertThat(sslFactory.getSslContext()).isNotNull();
        assertThat(sslFactory.getKeyManager()).isNotPresent();
        assertThat(sslFactory.getTrustManager()).isPresent();
    }

    @Test
    void createSslFactoryWithTwoWayAuthentication() {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLFactory sslFactory = victim.sslFactory(false, true,
                keyStorePath, keyStorePassword.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslFactory).isNotNull();
        assertThat(sslFactory.getSslContext()).isNotNull();
        assertThat(sslFactory.getKeyManager()).isPresent();
        assertThat(sslFactory.getTrustManager()).isPresent();
    }

    @Test
    void notCreateSslFactoryWhenOneWayAuthenticationAndTwoWayAuthenticationIsDisabled() {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLFactory sslFactory = victim.sslFactory(false, false,
                keyStorePath, keyStorePassword.toCharArray(), trustStorePath, trustStorePassword.toCharArray());

        assertThat(sslFactory).isNotNull();
        assertThat(sslFactory.getSslContext()).isNotNull();
        assertThat(sslFactory.getKeyManager()).isNotPresent();
        assertThat(sslFactory.getTrustManager()).isPresent();
    }

}
