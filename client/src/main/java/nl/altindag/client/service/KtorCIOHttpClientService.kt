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
