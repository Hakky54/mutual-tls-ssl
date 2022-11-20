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
package nl.altindag.client.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.network.tls.*
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_CIO_HTTP_CLIENT
import nl.altindag.ssl.util.KeyStoreUtils
import nl.altindag.ssl.util.TrustManagerUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KtorCIOHttpClientService(
        @Value("\${client.ssl.one-way-authentication-enabled:false}") oneWayAuthenticationEnabled: Boolean,
        @Value("\${client.ssl.two-way-authentication-enabled:false}") twoWayAuthenticationEnabled: Boolean,
        @Value("\${client.ssl.key-store:}") keyStorePath: String?,
        @Value("\${client.ssl.key-store-password:}") keyStorePassword: CharArray?,
        @Value("\${client.ssl.trust-store:}") trustStorePath: String?,
        @Value("\${client.ssl.trust-store-password:}") trustStorePassword: CharArray?
): KtorHttpClientService(
        HttpClient(CIO) {
            if (oneWayAuthenticationEnabled) {
                engine {
                    https {
                        trustManager = TrustManagerUtils.createTrustManager(KeyStoreUtils.loadKeyStore(trustStorePath, trustStorePassword))
                    }
                }
            } else if (twoWayAuthenticationEnabled) {
                engine {
                    https {
                        addKeyStore(KeyStoreUtils.loadKeyStore(keyStorePath, keyStorePassword), keyStorePassword)
                        trustManager = TrustManagerUtils.createTrustManager(KeyStoreUtils.loadKeyStore(trustStorePath, trustStorePassword))
                    }
                }
            }
        }
) {

    override fun getClientType(): ClientType = KTOR_CIO_HTTP_CLIENT

}
