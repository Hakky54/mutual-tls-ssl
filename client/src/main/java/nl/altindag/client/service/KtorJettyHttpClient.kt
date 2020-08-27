package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.jetty.Jetty
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_JETTY_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import nl.altindag.sslcontext.util.JettySslContextUtils
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KtorJettyHttpClient(
        @Autowired(required = false)
        sslFactory: SSLFactory?
): KtorHttpClientService(
        HttpClient(Jetty) {
            engine {
                sslContextFactory = sslFactory?.let { factory ->
                    JettySslContextUtils.forClient(factory)
                } ?: SslContextFactory.Client()
            }
        }
) {

    override fun getClientType(): ClientType = KTOR_JETTY_HTTP_CLIENT

}