package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE4_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.Apache4SslUtils
import org.apache.http.impl.client.HttpClients
import org.http4k.client.Apache4Client
import org.springframework.stereotype.Service

@Service
class Http4kApache4HttpClientService(
    sslFactory: SSLFactory
) : Http4kClientService(
    Apache4Client(
        client = HttpClients.custom()
            .setSSLSocketFactory(Apache4SslUtils.toSocketFactory(sslFactory))
            .build()
    )
) {

    override fun getClientType(): ClientType = HTTP4K_APACHE4_HTTP_CLIENT

}