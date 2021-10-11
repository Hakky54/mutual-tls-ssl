package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.jetty.Jetty
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_JETTY_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.JettySslUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KtorJettyHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
): KtorHttpClientService(
        HttpClient(Jetty) {
            sslFactory?.let { factory ->
                engine {
                    sslContextFactory = JettySslUtils.forClient(factory)
                }
            }
        }
) {

    override fun getClientType(): ClientType = KTOR_JETTY_HTTP_CLIENT

}
