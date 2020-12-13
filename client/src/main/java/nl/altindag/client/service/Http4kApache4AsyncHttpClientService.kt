package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.sslcontext.SSLFactory
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.http4k.client.Apache4AsyncClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Http4kApache4AsyncHttpClientService(
        @Autowired(required = false)
        val sslFactory: SSLFactory?
) : Http4kAsyncClientService(
        Apache4AsyncClient(
                client = sslFactory?.let { factory ->
                    HttpAsyncClients.custom()
                            .setSSLContext(factory.sslContext)
                            .setSSLHostnameVerifier(factory.hostnameVerifier)
                            .build()
                } ?: HttpAsyncClients.createDefault()
        )
) {

    override fun getClientType(): ClientType = ClientType.HTTP4K_APACHE4_ASYNC_HTTP_CLIENT

}