package nl.altindag.client.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.network.tls.*
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_CIO_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KtorCIOHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
): KtorHttpClientService(
        HttpClient(CIO) {
            sslFactory?.let { factory ->
                engine {
                    https {
                        factory.identities.getOrNull(0)?.let { keyStoreHolder ->
                            addKeyStore(keyStoreHolder.keyStore, keyStoreHolder.keyStorePassword)
                        }
                        trustManager = factory.trustManager.orElseThrow()
                    }
                }
            }
        }
) {

    override fun getClientType(): ClientType = KTOR_CIO_HTTP_CLIENT

}
