package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE5_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import nl.altindag.ssl.util.Apache5SslUtils
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.http4k.client.ApacheClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Http4kApache5HttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
) : Http4kClientService(
        ApacheClient(
                client = sslFactory?.let { factory ->
                    val socketFactory = Apache5SslUtils.toSocketFactory(sslFactory)
                    val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(socketFactory)
                            .build()

                    HttpClients.custom()
                            .setConnectionManager(connectionManager)
                            .build()
                } ?: HttpClients.createDefault()
        )
) {

    override fun getClientType(): ClientType = HTTP4K_APACHE5_HTTP_CLIENT

}