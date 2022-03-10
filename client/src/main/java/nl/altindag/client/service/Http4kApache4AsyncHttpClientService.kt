package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.ssl.SSLFactory
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.http4k.client.Apache4AsyncClient
import org.springframework.stereotype.Service

@Service
class Http4kApache4AsyncHttpClientService(
    sslFactory: SSLFactory
) : Http4kAsyncClientService(
    Apache4AsyncClient(
        client = HttpAsyncClients.custom()
            .setSSLContext(sslFactory.sslContext)
            .setSSLHostnameVerifier(sslFactory.hostnameVerifier)
            .build()
    )
) {

    override fun getClientType(): ClientType = ClientType.HTTP4K_APACHE4_ASYNC_HTTP_CLIENT

}