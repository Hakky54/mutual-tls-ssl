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
package nl.altindag.client.util;

import nl.altindag.ssl.SSLFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.mockito.Mockito;

public final class SSLFactoryTestHelper {

    public static SSLFactory createSSLFactory(boolean oneWayAuthenticationEnabled, boolean twoWayAuthenticationEnabled) {
        String keyStorePath = "keystores-for-unit-tests/identity.jks";
        String keyStorePassword = "secret";
        String trustStorePath = "keystores-for-unit-tests/truststore.jks";
        String trustStorePassword = "secret";

        SSLFactory.Builder sslFactoryBuilder = SSLFactory.builder();
        if (oneWayAuthenticationEnabled) {
            sslFactoryBuilder.withTrustMaterial(trustStorePath, trustStorePassword.toCharArray())
                    .withHostnameVerifier(new DefaultHostnameVerifier());
        }

        if (twoWayAuthenticationEnabled) {
            sslFactoryBuilder.withIdentityMaterial(keyStorePath, keyStorePassword.toCharArray())
                    .withTrustMaterial(trustStorePath, trustStorePassword.toCharArray())
                    .withHostnameVerifier(new DefaultHostnameVerifier());
        }
        return Mockito.spy(sslFactoryBuilder.build());
    }

    public static SSLFactory createBasic() {
        return Mockito.spy(
                SSLFactory.builder()
                .withDefaultTrustMaterial()
                .build()
        );
    }

}
