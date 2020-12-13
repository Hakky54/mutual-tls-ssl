package nl.altindag.client.service

import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.HTTP4K_APACHE5_ASYNC_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.core5.http.nio.ssl.BasicClientTlsStrategy
import org.http4k.client.ApacheAsyncClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Http4kApache5AsyncHttpClientService(
        @Autowired(required = false)
        val sslFactory: SSLFactory?
) : Http4kAsyncClientService(
        ApacheAsyncClient(
                client = sslFactory?.let { factory ->
                    PoolingAsyncClientConnectionManagerBuilder.create()
                            .setTlsStrategy(BasicClientTlsStrategy(factory.sslContext))
                            .build()
                }?.let { connectionManager ->
                    HttpAsyncClients.custom()
                            .setConnectionManager(connectionManager)
                            .build()
                } ?: HttpAsyncClients.createDefault()
        )
) {

    override fun getClientType(): ClientType = HTTP4K_APACHE5_ASYNC_HTTP_CLIENT

}