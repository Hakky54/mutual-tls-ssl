package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_JETTY_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.JettySslUtils
import org.eclipse.jetty.client.HttpClient
import org.http4k.client.JettyClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Http4kJettyHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
) : Http4kClientService(
        JettyClient(
                client = sslFactory?.let { factory ->
                    HttpClient(JettySslUtils.forClient(factory))
                } ?: HttpClient()
        )
) {

    override fun getClientType(): ClientType = HTTP4K_JETTY_HTTP_CLIENT

}