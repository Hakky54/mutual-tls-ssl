package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_JETTY_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import nl.altindag.sslcontext.util.JettySslContextUtils
import org.eclipse.jetty.client.HttpClient
import org.http4k.client.JettyClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Http4kJettyHttpClientService(
        @Autowired(required = false)
        val sslFactory: SSLFactory?
) : Http4kClientService(
        JettyClient(
                client = sslFactory?.let { factory ->
                    JettySslContextUtils.forClient(factory)
                }?.let { sslContextFactory ->
                    HttpClient(sslContextFactory)
                } ?: HttpClient()
        )
) {

    override fun getClientType(): ClientType = HTTP4K_JETTY_HTTP_CLIENT

}