package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_JETTY_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.JettySslUtils
import org.eclipse.jetty.client.HttpClient
import org.http4k.client.JettyClient
import org.springframework.stereotype.Service

@Service
class Http4kJettyHttpClientService(
        sslFactory: SSLFactory
) : Http4kClientService(
        JettyClient(
                client = HttpClient(JettySslUtils.forClient(sslFactory))
        )
) {

    override fun getClientType(): ClientType = HTTP4K_JETTY_HTTP_CLIENT

}